import { useSearchParams } from "react-router-dom"
import { useState, useEffect } from "react"
import { TopBar, MinionPanel, HexBoard, SidePanel, MinionsIMG } from "../assets"
import { useGameController } from "../game/useGameController"
import { getGameConfig, type GameConfig } from "../game/gameApi"
import type { GameMode, MinionConfig } from "../types/gameTypes"

import "../styles/gameplay.css"

import AppLayout from "../layouts/AppLayout"
import GameLayout from "../layouts/GameLayout"
import Modal from "../components/Modal.tsx"

export default function GameplayPage() {
    const [searchParams] = useSearchParams()
    const modeParam = searchParams.get("mode") || "DUEL"
    const modeFromUrl = modeParam.toUpperCase() as GameMode
    const role = searchParams.get("role")
    const gameId = searchParams.get("gameId") || ""
    const isDuel = modeFromUrl === "DUEL"
    const isPlayer1 = role === "host" || !isDuel
    const isPlayer2 = role === "guest"

    const [gameConfig, setGameConfig] = useState<GameConfig | null>(null)

    const {
        gameState,
        loading,
        error,
        currentPlayerBudget,
        selectedHex,
        adjacentHexes,
        hasBoughtHex,
        hasSpawnedMinion,

        showBuyConfirm,
        handleRequestBuyHex,
        handleConfirmBuyHex,
        handleCancelBuyHex,

        selectedMinionType,
        handleSelectMinion,

        showSpawnConfirm,
        handleConfirmSpawn,
        handleCancelSpawn,

        handleSelectHex,
        handleEndTurn,
        handleEndGame,

        isAutoPlaying,

        showErrorModal,
        errorMessage,
        closeErrorModal

    } = useGameController(modeFromUrl, gameId)

    useEffect(() => {
        getGameConfig().then(setGameConfig).catch(console.error)
    }, [])

    if (loading) {
        return (
            <AppLayout>
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
                    <p>Loading game...</p>
                </div>
            </AppLayout>
        )
    }

    if (error || !gameState) {
        return (
            <AppLayout>
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
                    <p>Error: {error || "No game state"}</p>
                </div>
            </AppLayout>
        )
    }

    const getMinionAvatar = (typeName: string): string => {
        const avatars = MinionsIMG as Record<string, string>
        return avatars[typeName] || ""
    }

    const minions: MinionConfig[] = (gameState.players?.[1]?.minionTypes || []).map(t => ({
        type: t.name,
        customName: t.name,
        def: t.defense,
        strategy: t.strategy,
        isValid: true,
        avatar: getMinionAvatar(t.name)
    }))

    const selectedMinionConfig = selectedMinionType 
        ? minions.find(m => m.type === selectedMinionType) ?? null 
        : null

    const playerLabel = gameState.currentPlayer === 1 ? "Player 1" : "Player 2"
    
    const isSetupPhase = gameState.phase === "SETUP"
    const isMyTurn = isDuel 
        ? isSetupPhase 
            ? (isPlayer1 && gameState.currentPlayer === 1) || (isPlayer2 && gameState.currentPlayer === 2)
            : (gameState.currentPlayer === 1 && isPlayer1) || (gameState.currentPlayer === 2 && isPlayer2)
        : gameState.currentPlayer === 1

    const canInteract = isMyTurn
    const canBuyHex = isMyTurn && !hasBoughtHex
    const canSpawnMinion = isMyTurn && !hasSpawnedMinion

    // In SOLITAIRE mode, human (P1) can always end turn to let bot play
    const canEndTurn = isDuel ? isMyTurn : true

    return (
        <AppLayout>
            <GameLayout
                header={
                    <TopBar
                        turn={gameState.globalTurn}
                        currentPlayer={playerLabel}
                        budget={currentPlayerBudget}
                        mode={modeParam.charAt(0).toUpperCase() + modeParam.slice(1).toLowerCase() as any}
                    />
                }

                board={
                    <HexBoard
                        board={gameState.board}
                        selectedHex={canInteract ? selectedHex : null}
                        onHexClick={isMyTurn ? handleSelectHex : () => {}}
                        buyableHexes={
                            gameState.players[
                                gameState.currentPlayer
                                ].spawnableHexes
                        }
                        adjacentHexes={canBuyHex ? adjacentHexes : []}
                    />
                }

                side={
                    <SidePanel
                        selectedHex={canInteract ? selectedHex : null}
                        onBuyHexRequest={canBuyHex ? handleRequestBuyHex : () => {}}
                        onEndTurn={canEndTurn ? handleEndTurn : () => {}}
                        onEndGame={handleEndGame}
                        isAutoPlaying={isAutoPlaying}
                        spawnsLeft={gameState?.currentPlayerSpawnsLeft ?? 0}
                        gameConfig={gameConfig}
                        logs={gameState?.turnLogs ?? []}
                        mode={modeParam.charAt(0).toUpperCase() + modeParam.slice(1).toLowerCase() as "Duel" | "Solitaire" | "Auto"}
                    />
                }

                log={
                    <MinionPanel
                        minions={minions}
                        selected={canInteract ? selectedMinionConfig : null}
                        onSelect={canSpawnMinion ? (m) => handleSelectMinion(m.type) : () => {}}
                    />
                }
            />

            <Modal isOpen={showBuyConfirm} onClose={handleCancelBuyHex}>
                <h2>Buy this hex?</h2>
                <div className="modal-actions"> 
                    <button className="modal-btn modal-cancel-btn" onClick={handleCancelBuyHex}>Cancel</button>
                    <button className="modal-btn modal-confirm-btn" onClick={handleConfirmBuyHex}>Confirm</button>
                </div>
            </Modal>

            <Modal isOpen={showSpawnConfirm} onClose={handleCancelSpawn}>
                <h2>Spawn this minion?</h2>
                <div className="modal-actions">
                    <button className="modal-btn modal-cancel-btn" onClick={handleCancelSpawn}>Cancel</button>
                    <button className="modal-btn modal-confirm-btn" onClick={handleConfirmSpawn}>Confirm</button>
                </div>
            </Modal>

            <Modal isOpen={showErrorModal} onClose={closeErrorModal}>
                <h2>Action Failed</h2>
                <p style={{ color: "#EF4444", margin: "16px 0", textAlign: "center" }}>{errorMessage}</p>
                <div className="modal-actions">
                    <button className="modal-btn modal-confirm-btn" onClick={closeErrorModal}>OK</button>
                </div>
            </Modal>
        </AppLayout>
    )
}