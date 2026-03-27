import type { HexCoord } from "../types/gameTypes"
import HexCell from "./HexCell"
import "../styles/hexBoard.css"

type Props = {
    board: HexCoord[]
    onHexClick?: (hex: HexCoord) => void
    selectedHex?: HexCoord | null
    buyableHexes: string[]
    adjacentHexes?: string[]
}

export default function HexBoard({
                                     board,
                                     onHexClick,
                                     selectedHex,
                                     buyableHexes,
                                     adjacentHexes = []
                                 }: Props) {

    const size = 35
    const width = size * 2
    const height = Math.sqrt(3) * size

    const horizontalStep = width * 0.75
    const verticalStep = height

    const cols = 8

    const getHexKey = (hex: HexCoord) => `${hex.col},${hex.row}`
    const isSelected = (hex: HexCoord) => 
        selectedHex?.row === hex.row && selectedHex?.col === hex.col
    const isBuyable = (hex: HexCoord) => 
        buyableHexes?.includes(getHexKey(hex))
    const isAdjacent = (hex: HexCoord) => 
        adjacentHexes?.includes(getHexKey(hex))

    return (
        <div
            className="board"
            style={{
                position: "relative",
                width: cols * horizontalStep + size,
                height: 8 * verticalStep + height / 2 + 20
            }}
        >
            {board.map((hex) => {
                const x = hex.col * horizontalStep
                const y = hex.row * verticalStep + 
                    (hex.col % 2 === 0 ? height / 2 : 0)

                return (
                    <div
                        key={getHexKey(hex)}
                        style={{
                            position: "absolute",
                            left: x,
                            top: y
                        }}
                        onClick={() => onHexClick?.(hex)}
                    >
                        <div style={{ position: "relative" }}>
                            <HexCell
                                owner={hex.owner}
                                minion={hex.minion}
                                isSelected={isSelected(hex)}
                                isBuyable={isBuyable(hex)}
                                isAdjacent={isAdjacent(hex)}
                            />
                            <div className="hex-coords">
                                {hex.col},{hex.row}
                            </div>
                        </div>
                    </div>
                )
            })}
        </div>
    )
}