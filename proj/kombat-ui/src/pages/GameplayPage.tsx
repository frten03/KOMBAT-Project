import { useNavigate, useLocation, useSearchParams } from "react-router-dom"
import { useEffect, useState } from "react"

import TopBar from "../components/TopBar"
import MinionPanel from "../components/MinionPanel"
import HexBoard from "../components/HexBoard"
import SidePanel from "../components/SidePanel"

import "../styles/gameplay.css"
import AppLayout from "../layouts/AppLayout"
import GameLayout from "../layouts/GameLayout"

import type { GameState, GameMode } from "../types/gameTypes"
import { mockGameState } from "../mock/mockGameState"

export default function GameplayPage() {
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams] = useSearchParams()

    const modeFromUrl = (searchParams.get("mode") || "Duel") as GameMode


    const [gameState, setGameState] = useState<GameState>({
        ...mockGameState,
        mode: modeFromUrl
    })


    useEffect(() => {
        let configs = []

        if (location.state?.selectedMinions) {
            configs = location.state.selectedMinions
            localStorage.setItem("gameMinions", JSON.stringify(configs))
        } else {
            const saved = localStorage.getItem("gameMinions")
            if (saved) configs = JSON.parse(saved)
        }

        setGameState(prev => ({
            ...prev,
            minions: configs
        }))
    }, [location.state])


    const handleEndTurn = () => {
        setGameState(prev => ({
            ...prev,
            turn: prev.turn + 1,
            currentPlayer:
                prev.currentPlayer === "Player 1"
                    ? "Player 2"
                    : "Player 1",
            log: [
                ...prev.log,
                `Turn ${prev.turn} ended.`
            ]
        }))
    }

    const handleEndGame = () => {
        localStorage.removeItem("gameMinions")
        navigate("/end")
    }


    return (
        <AppLayout>
            <GameLayout
                header={
                    <TopBar
                        turn={gameState.turn}
                        currentPlayer={gameState.currentPlayer}
                        budget={
                            gameState.players[
                                gameState.currentPlayer
                                ].budget
                        }
                        mode={gameState.mode}
                    />
                }
                board={<HexBoard board={gameState.board} />}
                side={
                    <SidePanel
                        onEndTurn={handleEndTurn}
                        onEndGame={handleEndGame}
                    />
                }
                log={<MinionPanel minions={gameState.minions} />}
            />
        </AppLayout>
    )
}