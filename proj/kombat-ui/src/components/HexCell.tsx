
type Props = {
    owner: "Player 1" | "Player 2" | null
    selectable?: boolean
    onClick?: () => void
}

export default function HexCell({ owner, selectable = false,onClick }: Props) {

    const size = 35
    const width = size * 2
    const height = Math.sqrt(3) * size

    const points = `
    ${size/2},0
    ${size*1.5},0
    ${width},${height/2}
    ${size*1.5},${height}
    ${size/2},${height}
    0,${height/2}
  `

    const ownerClass =
        owner === "Player 1"
            ? "p1"
            : owner === "Player 2"
                ? "p2"
                : "none"

    return (
        <svg
            className={`hex ${ownerClass} ${selectable ? "selectable" : ""}`}
            onClick={onClick}
            style={{ cursor: "pointer" }}
        >
            <polygon points={points} />
        </svg>
    )
}