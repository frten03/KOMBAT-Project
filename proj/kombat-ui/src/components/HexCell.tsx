import type { Minion } from "../types/gameTypes"
import { MinionsIMG } from "../assets"
import "../styles/hexCell.css"

type Props = {
    owner: number | null
    minion: Minion | null
    isSelected?: boolean
    isBuyable?: boolean
    isAdjacent?: boolean
}

export default function HexCell({
                                    owner,
                                    minion,
                                    isSelected = false,
                                    isBuyable = false,
                                    isAdjacent = false
                                }: Props) {

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
        owner === 1
            ? "p1"
            : owner === 2
                ? "p2"
                : isAdjacent
                    ? "adjacent"
                    : "none"

    const getMinionImage = (typeName: string | undefined): string | undefined => {
        if (!typeName) return undefined
        return (MinionsIMG as Record<string, string>)[typeName]
    }

    const getHpColor = (hp: number, maxHp: number) => {
        if (hp > maxHp * 0.5) return "#22C55E"
        if (hp > maxHp * 0.25) return "#FACC15"
        return "#EF4444"
    }

    return (
        <div 
            className={`hex-cell ${ownerClass} ${isSelected ? "selected" : ""} ${isBuyable ? "buyable" : ""}`}
            style={{ position: "relative", width, height }}
        >
            <svg
                className="hex-shape"
                viewBox={`0 0 ${width} ${height}`}
                style={{ width: "100%", height: "100%" }}
            >
                <polygon points={points} />
            </svg>
            {minion && (
                <div className="minion-container">
                    <img
                        src={getMinionImage(minion.typeName)}
                        alt={minion.typeName}
                        className="minion-image"
                        style={{ transform: minion.owner === 2 ? 'scaleX(-1)' : 'none' }}
                    />
                    <div className="hp-bar-container">
                        <div 
                            className="hp-bar-fill" 
                            style={{ 
                                width: `${(minion.hp / minion.maxHp) * 100}%`,
                                backgroundColor: getHpColor(minion.hp, minion.maxHp)
                            }}
                        />
                    </div>
                </div>
            )}
        </div>
    )
}
