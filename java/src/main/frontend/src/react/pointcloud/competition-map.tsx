import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

export type Competition = {
    id: string;
    title: string;
    coordinateX: number;
    coordinateY: number;
};

type Props = {
    data: Competition[];
    clickListener: (identifier: string) => void;
    closestChangeListener: (identifier: string) => void;
    width: number;
    height: number;
};

const CompetitionMap: React.FC<Props> = (
    {
        data,
        clickListener,
        closestChangeListener,
        width,
        height
     }) => {
    const svgRef = useRef<SVGSVGElement | null>(null);

    useEffect(() => {
        if (!svgRef.current || data.length === 0) return;

        // -----------------------------
        // Compute scales
        // -----------------------------
        const xExtent = d3.extent(data, d => d.coordinateX) as [number, number];
        const yExtent = d3.extent(data, d => d.coordinateY) as [number, number];

        const xScale = d3
            .scaleLinear()
            .domain(xExtent)
            .range([50, width - 50]);

        const yScale = d3
            .scaleLinear()
            .domain(yExtent)
            .range([height - 50, 50]);

        const svg = d3.select(svgRef.current);
        svg.selectAll("*").remove();

        const quadtree = d3.quadtree<Competition>()
            .x(d => xScale(d.coordinateX))
            .y(d => yScale(d.coordinateY))
            .addAll(data);

        const g = svg.append("g");

        // -----------------------------
        // Draw points
        // -----------------------------
        g.selectAll("circle")
            .data(data)
            .enter()
            .append("circle")
            .attr("cx", d => xScale(d.coordinateX))
            .attr("cy", d => yScale(d.coordinateY))
            .attr("r", 5)
            .attr("fill", "#4f46e5")
            .attr("opacity", 0.8)
            .on("click", (_, d) => clickListener(d.id))
            .append("title")
            .text(d => d.title)

        const highlight = g.append("circle")
            .attr("r", 10)
            .attr("fill", "none")
            .attr("stroke", "orange")
            .attr("stroke-width", 2)
            .style("pointer-events", "none")
            .style("opacity", 0);

        const titleText = svg.append("text")
            .attr("x", width / 2)
            .attr("y", 30)
            .attr("text-anchor", "middle")
            .attr("font-size", "18px")
            .attr("font-weight", "bold")
            .text("");
        // -----------------------------
        // Zoom behavior
        // -----------------------------
        let currentTransform = d3.zoomIdentity;
        const zoom = d3.zoom<SVGSVGElement, unknown>()
            .scaleExtent([0.5, 10])
            .on("zoom", (event) => {
                currentTransform = event.transform;
                g.attr("transform", currentTransform.toString());
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

    }, [data, width, height]);

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