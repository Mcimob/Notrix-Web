export type Stage = {
    id: number;
    count: number;
};

export type Transitions = number[][];

export type Label = {
    id: number;
    name: string;
    groupName: string;
    color: string;
    stroke: string;
    strokeDasharray: string;
};

export type Props = {
    stages: Stage[];
    transitions: Transitions;
    labels: Label[];
};