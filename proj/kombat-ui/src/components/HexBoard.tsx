import type { Hex } from "../types/gameTypes"
import HexCell from "./HexCell"
import "../styles/HexBoard.css"

type Props = {
    board: Hex[]
    onHexClick?: (hex: Hex) => void
}

export default function HexBoard({ board, onHexClick }: Props) {

    const size = 35
    const width = size * 2
    const height = Math.sqrt(3) * size

    const horizontalStep = size * 1.5
    const verticalStep = height * 0.75

    return (
        <div
            className="board"
            style={{
                width: horizontalStep * 8 + size,
                height: verticalStep * 8 + height
            }}
        >{board.map((hex, index) => {

            const row = Math.floor(index / 8)
            const col = index % 8

            const x = col * (width * 0.75)

            const baseOffset = height / 2

            const y =
                row * height +
                (col % 2 === 0 ? height / 2 : 0) +
                baseOffset

            return (
                <div
                    key={hex.id}
                    style={{
                        position: "absolute",
                        left: x,
                        top: y
                    }}
                >
                    <HexCell owner={hex.owner} />
                </div>
            )
        })}
        </div>
    )
}