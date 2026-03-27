import { useRef, useEffect } from "react"
import type { HexCoord } from "../types/gameTypes"
import type { GameConfig } from "../game/gameApi"

interface Props {
    selectedHex: HexCoord | null
    onBuyHexRequest: (hex: HexCoord) => void
    onEndTurn: () => void
    onEndGame: () => void
    isAutoPlaying?: boolean
    spawnsLeft?: number
    gameConfig?: GameConfig | null
    logs?: string[]
    mode?: GameMode
}

type GameMode = "Duel" | "Solitaire" | "Auto"

export default function SidePanel({
    selectedHex, 
    onBuyHexRequest, 
    onEndTurn, 
    onEndGame,
    isAutoPlaying = false,
    spawnsLeft = 0,
    gameConfig,
    logs = [],
    mode = "Duel"
}: Props) {

    const hexCost = gameConfig?.hexPurchaseCost ?? 0
    const logEndRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        logEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }, [logs])

    return (
        <div className="right-sidebar">

            <div className="card turn-card">
                <h3>Turn Resources</h3>

                <div>Interest Rate : {gameConfig?.interestPct ?? 0}%</div>
                <div>Spawns Left : {spawnsLeft}</div>

                <div>
                    Hex Expansion : {hexCost}
                </div>

                <button
                    className="buy-btn"
                    onClick={() => selectedHex && onBuyHexRequest(selectedHex)}
                    disabled={!selectedHex}>
                    Buy Hex
                </button>
            </div>

            <div className="card log-card">
                <h3>Log Panel :</h3>

                {logs.map((log, index) => (
                    <div key={index}>{log}</div>
                ))}
                <div ref={logEndRef} />
            </div>

            <div className="action-buttons">
                {isAutoPlaying && (
                    <button className="auto-play-indicator">
                        Bot...
                    </button>
                )}

                <button
                    className="end-game-btn"
                    onClick={onEndGame}
                >
                    End Game
                </button>

                {mode !== "Auto" && !isAutoPlaying && (
                    <button
                        className="end-turn-btn"
                        onClick={onEndTurn}
                    >
                        End Turn
                    </button>
                )}
            </div>
        </div>
    )
}