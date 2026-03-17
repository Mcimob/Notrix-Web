import React, {useEffect, useRef} from "react";
import * as d3 from "d3";

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

        const base = svg.append("g");
        const g = base.append("g");

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
        // Main Canvas scales
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

        // Minimap Scales
        const minimapWidth = 200;
        const minimapHeight = 150;

        const scaleX = minimapWidth / width;
        const scaleY = minimapHeight / height;

        const miniX = d3.scaleLinear()
            .domain(xExtent)
            .range([0, minimapWidth]);

        const miniY = d3.scaleLinear()
            .domain(yExtent)
            .range([minimapHeight, 0]);

        // Quadtree for efficient competition searching
        const quadtree = d3.quadtree<Competition>()
            .x(d => xScale(d.coordinateX))
            .y(d => yScale(d.coordinateY))
            .addAll(competitions);

        const drawClusters = () => {

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
        }

        const drawPoints = () => {
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
        }

        const drawMinimap = () => {
            const minimap = svg.append("g")
                .attr("transform", `translate(${width - minimapWidth - 10}, ${height - minimapHeight - 10})`)
                .style("filter", "drop-shadow(0px 2px 4px rgba(0,0,0,0.2))")
                .attr("opacity", 0.8);

            minimap.append("rect")
                .attr("width", minimapWidth)
                .attr("height", minimapHeight)
                .attr("fill", "#fff")
                .attr("stroke", "#ccc");

            const miniContent = minimap.append(() =>
                base.node()!.cloneNode(true) as SVGGElement
            );
            miniContent.selectAll("*").style("pointer-events", "none");
            miniContent.selectAll("text").remove();

            miniContent.attr("transform", `scale(${scaleX}, ${scaleY})`);

            const viewport = minimap.append("rect")
                .attr("fill", "var(--lumo-primary-text-color)")
                .attr("opacity", "0.2")
                .attr("stroke", "var(--lumo-primary-text-color)")
                .attr("stroke-width", 1.5)

            svg.append("defs")
                .append("clipPath")
                .attr("id", "minimap-clip")
                .append("rect")
                .attr("width", minimapWidth)
                .attr("height", minimapHeight);
            minimap.attr("clip-path", "url(#minimap-clip)");

            minimap.on("click", (event) => {
                const [mx, my] = d3.pointer(event);

                const targetX = miniX.invert(mx);
                const targetY = miniY.invert(my);

                const scale = currentTransform.k;

                const newTransform = d3.zoomIdentity
                    .translate(
                        width / 2 - xScale(targetX) * scale,
                        height / 2 - yScale(targetY) * scale
                    )
                    .scale(scale);

                svg.transition()
                    .duration(500)
                    .call(zoom.transform, newTransform);
            });

            return viewport;
        }

        const drawClusterTexts = () => {
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
        }

        drawClusters();
        drawPoints();
        drawClusterTexts();
        const viewport = drawMinimap();

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

                // visible bounds in data space
                const x0 = xScale.invert((0 - currentTransform.x) / currentTransform.k);
                const x1 = xScale.invert((width - currentTransform.x) / currentTransform.k);
                const y0 = yScale.invert((height - currentTransform.y) / currentTransform.k);
                const y1 = yScale.invert((0 - currentTransform.y) / currentTransform.k);

                const xMin = Math.min(x0, x1);
                const xMax = Math.max(x0, x1);
                const yMin = Math.min(y0, y1);
                const yMax = Math.max(y0, y1);

                viewport
                    .attr("x", miniX(xMin))
                    .attr("y", miniY(yMax))
                    .attr("width", miniX(xMax) - miniX(xMin))
                    .attr("height", miniY(yMin) - miniY(yMax));
            });

        svg.call(zoom);
        svg.call(zoom.transform, d3.zoomIdentity);

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