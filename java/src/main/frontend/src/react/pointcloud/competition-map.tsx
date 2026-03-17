import React, { useRef, useEffect } from "react";
import * as d3 from "d3";
import {schemePaired} from "d3-scale-chromatic";

export type Competition = {
    id: string;
    title: string;
    coordinateX: number;
    coordinateY: number;
    totalSubmissions: number;
    clusterId: number;
};

export type Cluster = {
    id: number;
    description: string;
    centroidX: number;
    centroidY: number;
    radiusX: number;
    radiusY: number;
    stdX: number;
    stdY: number;
}

type Props = {
    competitions: Competition[];
    clusters: Cluster[];
    clickListener: (identifier: string) => void;
    closestChangeListener: (identifier: string) => void;
    width: number;
    height: number;
};

const CompetitionMap: React.FC<Props> = (
    {
        competitions,
        clusters,
        clickListener,
        closestChangeListener,
        width,
        height
     }) => {
    const svgRef = useRef<SVGSVGElement | null>(null);

    useEffect(() => {
        if (!svgRef.current) return;

        const svg = d3.select(svgRef.current);
        svg.selectAll("*").remove();

        const g = svg.append("g");

        const titleText = svg.append("text")
            .attr("x", width / 2)
            .attr("y", 30)
            .attr("text-anchor", "middle")
            .attr("font-size", "18px")
            .attr("font-weight", "bold")
            .text("");
        if (competitions.length === 0 || clusters.length === 0) {
            titleText.text("Loading Competitions...")
            return;
        }

        const cluster_map = [];
        clusters.forEach(c => cluster_map[c.id] = c);

        const maxSubmissions = Math.max(...(competitions.map(c => c.totalSubmissions)));

        // -----------------------------
        // Compute scales
        // -----------------------------
        const xExtent = d3.extent(competitions, d => d.coordinateX) as [number, number];
        const yExtent = d3.extent(competitions, d => d.coordinateY) as [number, number];

        const xScale = d3
            .scaleLinear()
            .domain(xExtent)
            .range([50, width - 50]);

        const yScale = d3
            .scaleLinear()
            .domain(yExtent)
            .range([height - 50, 50]);

        const quadtree = d3.quadtree<Competition>()
            .x(d => xScale(d.coordinateX))
            .y(d => yScale(d.coordinateY))
            .addAll(competitions);

        // -----------------------------
        // Draw clusters
        // -----------------------------

        const scaleEllipseX = (c: Cluster, r: number) => xScale(c.centroidX + r) - xScale(c.centroidX);
        const scaleEllipseY = (c: Cluster, r: number) => yScale(c.centroidY) - yScale(c.centroidY + r);

        const opacityLevels = [0.3, 0.15, 0.07]; // inner to outer

        g.selectAll("g.cluster")
            .data(clusters)
            .enter()
            .append("g")
            .attr("class", "cluster")
            .each(function(c) {
                const gCluster = d3.select(this);

                // draw ellipses: mean radius, mean ± 1 std, mean ± 2 std
                const radii = [
                    [c.radiusX, c.radiusY],               // mean
                    [c.radiusX + c.stdX, c.radiusY + c.stdY],
                    [c.radiusX + 2*c.stdX, c.radiusY + 2*c.stdY]
                ];

                radii.forEach((r, i) => {
                    gCluster.append("ellipse")
                        .attr("cx", xScale(c.centroidX))
                        .attr("cy", yScale(c.centroidY))
                        .attr("rx", scaleEllipseX(c, r[0]))
                        .attr("ry", scaleEllipseY(c, r[1]))
                        .attr("fill", d3.hsl(c.id * 360 / clusters.length, 0.5, 0.6).toString())
                        .attr("opacity", opacityLevels[i])
                        .style("pointer-events", "none");
                });
            });

        // -----------------------------
        // Draw points
        // -----------------------------
        const maxCircleSize = 10;
        const minCircleSize = 3;
        g.selectAll("circle")
            .data(competitions)
            .enter()
            .append("circle")
            .attr("cx", d => xScale(d.coordinateX))
            .attr("cy", d => yScale(d.coordinateY))
            .attr("r", d => minCircleSize * Math.pow((maxCircleSize / minCircleSize), d.totalSubmissions / maxSubmissions))
            .attr("fill", d => d3.hsl(d.clusterId * 360 / clusters.length, 0.5, 0.6).toString())
            .attr("opacity", 0.8)
            .on("click", (_, d) => clickListener(d.id))
            .append("title")
            .text(d => d.title)

        const highlight = g.append("circle")
            .attr("r", maxCircleSize)
            .attr("fill", "none")
            .attr("stroke", "orange")
            .attr("stroke-width", 2)
            .style("pointer-events", "none")
            .style("opacity", 0);

    g.selectAll("text")
        .data(clusters)
        .enter()
        .append("text")
        .attr("x", c => xScale(c.centroidX))
        .attr("y", c => yScale(c.centroidY))
        .attr("text-anchor", "middle")
        .attr("font-size", "18px")
        .attr("font-weight", "bold")
        .attr("opacity", 0.8)
        .text(c => c.description);

        // -----------------------------
        // Zoom behavior
        // -----------------------------
        let currentTransform = d3.zoomIdentity;
        const zoom = d3.zoom<SVGSVGElement, unknown>()
            .scaleExtent([0.5, 10])
            .on("zoom", (event) => {
                currentTransform = event.transform;
                g.transition()
                    .duration(50) // small delay for smoothing
                    .ease(d3.easeCubicOut)
                    .attr("transform", currentTransform.toString());
            });

        svg.call(zoom);

        let lastClosest : Competition;
        svg.on("mousemove", (event) => {

            const [mx, my] = d3.pointer(event);

            const tx = currentTransform.invertX(mx);
            const ty = currentTransform.invertY(my);

            const closest = quadtree.find(tx, ty, 40);

            if (closest) {
                if (lastClosest != closest) {
                    closestChangeListener(closest.id)
                    lastClosest = closest;
                    titleText.text(closest.title);

                    highlight
                        .attr("cx", xScale(closest.coordinateX))
                        .attr("cy", yScale(closest.coordinateY))
                        .style("opacity", 1);
                }
            } else {
                highlight.style("opacity", 0);
            }
        });

    }, [competitions, clusters, width, height]);

    return (
        <svg
            ref={svgRef}
            width={width}
            height={height}
            style={{
                border: "1px solid #ddd",
                background: "#fafafa"
            }}
        />
    );
};

export default CompetitionMap;