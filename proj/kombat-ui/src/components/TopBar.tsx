
type GameMode = "Duel" | "Solitaire" | "Auto"
type Player = "Player 1" | "Player 2"

type TopBarProps = {
    turn: number
    currentPlayer: Player
    budget: number
    mode: GameMode
}

export default function TopBar({
                                   turn,
                                   currentPlayer,
                                   budget,
                                   mode
                               }: TopBarProps) {

    const items = [
        `Turn: ${turn}`,
        `Current Player: ${currentPlayer}`,
        `Budget: ${budget.toFixed(2)}`,
        `Mode: ${mode}`
    ]

    return (
        <div className="top-bar">
            {items.map((item, index) => (
                <span key={index} className="top-item">
                    {item}
                </span>
            ))}
        </div>
    )
}