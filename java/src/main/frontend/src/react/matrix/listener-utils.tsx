import React from "react";

export const addListener = (name: string, handler: (e: CustomEvent) => void, target: EventTarget) => {
    return React.useEffect(() => {
        const h = (e: Event) => {
            handler(e as CustomEvent);
        };
        target.addEventListener(name, h);
        return () => target.removeEventListener(name, h);
    }, []);
}