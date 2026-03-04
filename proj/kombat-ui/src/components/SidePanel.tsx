
interface Props {
    onEndTurn: () => void
    onEndGame: () => void
}

export default function SidePanel({
                                      onEndTurn,
                                      onEndGame
                                  }: Props) {

    // ตอนนี้เป็น mock ไว้ก่อน
    const interestRate = "X%"
    const spawnsLeft = "XX"
    const hexCost = 100

    const logs = [
        "Turn 5: Player 1 gained +90 budget",
        "Minion #1 moved up left (cost 1)",
        "Minion #2 shot down (cost 21)",
        "Minion #3 died",
    ]

    return (
        <div className="right-sidebar">

            <div className="card turn-card">
                <h3>Turn Resources</h3>

                <div>- Interest Rate {interestRate}</div>
                <div>- Spawns Left {spawnsLeft}</div>

                <div>
                    Hex Expansion : {hexCost}
                </div>

                <button className="buy-btn">
                    Buy Hex
                </button>
            </div>

            <div className="card log-card">
                <h3>Log Panel :</h3>

                {logs.map((log, index) => (
                    <div key={index}>{log}</div>
                ))}
            </div>

            <div className="action-buttons">

                <button
                    className="end-game-btn"
                    onClick={onEndGame}
                >
                    End Game
                </button>

                <button
                    className="end-turn-btn"
                    onClick={onEndTurn}
                >
                    End Turn
                </button>

            </div>
        </div>
    )
}