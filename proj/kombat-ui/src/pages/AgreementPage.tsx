import { useLocation, useNavigate, useSearchParams } from "react-router-dom"
import { useEffect, useState } from "react"

import { MinionsIMG } from "../assets";

import "../styles/agreement.css"
import AppLayout from "../layouts/AppLayout"
import CenteredLayout from "../layouts/CenteredLayout"
import Modal from "../components/Modal.tsx"

import type { MinionConfig } from "../types/gameTypes"
import { selectTypes, getGameConfig, getRoomStatus, agree, disagree, type GameConfig, type MinionTypeDTO } from "../game/gameApi"

export default function AgreementPage() {
    const location = useLocation()
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()

    const mode = searchParams.get("mode")
    const role = searchParams.get("role")
    const gameId = searchParams.get("gameId")
    const isDuel = mode === "duel"
    const isHost = role === "host"

    const [selectedMinions, setSelectedMinions] = useState<MinionConfig[]>([])
    const [hostConfig, setHostConfig] = useState<MinionTypeDTO[] | null>(null)
    const [loading, setLoading] = useState(false)
    const [gameConfig, setGameConfig] = useState<GameConfig | null>(null)
    const [showStrategyModal, setShowStrategyModal] = useState(false)
    const [selectedStrategy, setSelectedStrategy] = useState<MinionTypeDTO | null>(null)

    useEffect(() => {
        if (location.state?.selectedMinions) {
            setSelectedMinions(location.state.selectedMinions)
            localStorage.setItem(
                "agreementMinions",
                JSON.stringify(location.state.selectedMinions)
            )
        } else {
            const saved = localStorage.getItem("agreementMinions")
            if (saved) {
                setSelectedMinions(JSON.parse(saved))
            }
        }
    }, [location.state])

    useEffect(() => {
        getGameConfig().then(setGameConfig).catch(console.error)
    }, [])

    useEffect(() => {
        if (!isDuel || !gameId) return

        const fetchHostConfig = async () => {
            try {
                const status = await getRoomStatus(gameId)
                if (status.hostConfig && status.hostConfig.length > 0) {
                    setHostConfig(status.hostConfig)

                    const configs = status.hostConfig.map(hc => ({
                        type: hc.name,
                        customName: hc.name,
                        def: hc.defense,
                        strategy: hc.strategy,
                        isValid: true,
                        avatar: (MinionsIMG as any)[hc.name] || ""
                    }))

                    setSelectedMinions(configs)

                    localStorage.setItem("agreementMinions", JSON.stringify(configs))
                }
            } catch (err) {
                console.error("Failed to fetch host config:", err)
            }
        }

        fetchHostConfig()
    }, [isDuel, gameId, isHost])

    useEffect(() => {
        if (!isDuel || !gameId) return

        let lastConfigSubmitted = true

        const pollInterval = setInterval(async () => {
            try {
                const status = await getRoomStatus(gameId)
                
                if (isHost && lastConfigSubmitted && !status.configSubmitted) {
                    navigate(`/waiting?mode=duel&role=host&gameId=${gameId}`)
                    return
                }
                lastConfigSubmitted = status.configSubmitted
                
                if (status.hostAgreed && status.guestAgreed) {
                    navigate(`/gameplay?mode=duel&role=${role}&gameId=${gameId}`)
                }
            } catch (err) {
                console.error("Polling error:", err)
            }
        }, 1000)

        return () => clearInterval(pollInterval)
    }, [isDuel, gameId, navigate, role, isHost])

    const handleViewStrategy = (minion: MinionTypeDTO) => {
        setSelectedStrategy(minion)
        setShowStrategyModal(true)
    }

    const handleAgree = async () => {
        setLoading(true)
        try {
            if (isDuel && gameId) {
                await agree(gameId, role || "guest")
            } else if (gameId) {
                const typeNames = selectedMinions.map(m => m.type)
                await selectTypes(gameId, typeNames)

                navigate(`/gameplay?mode=${mode}&role=${role}&gameId=${gameId}`)
            } else {
                navigate(`/gameplay?mode=${mode}&role=${role}`, {
                    state: { selectedMinions }
                })
            }
        } catch (err) {
            console.error("Failed to agree/select types:", err)
            setLoading(false)
        }
    }

    const handleDisagree = async () => {
        if (isDuel && gameId && !isHost) {
            try {
                await disagree(gameId)
                navigate(`/waiting?mode=duel&role=guest&gameId=${gameId}`, { replace: true })
            } catch (err) {
                console.error("Failed to disagree:", err)
                navigate(`/waiting?mode=duel&role=guest&gameId=${gameId}`, { replace: true })
            }
        } else {
            localStorage.removeItem("agreementMinions")
            if (mode === "solitaire" || mode === "auto") {
                navigate(`/setup?mode=${mode}&gameId=${gameId}`, { replace: true })
            }
        }
    }

    const displayMinions = isDuel && !isHost && hostConfig 
        ? hostConfig.map(hc => ({
            name: hc.name,
            defense: hc.defense,
            strategy: hc.strategy
        }))
        : selectedMinions

    return (
        <AppLayout><CenteredLayout><div className="agreement-page">
            <div className="agreement-content">

                <h1 className="text-heading agreement-rules-title">
                    Game Rules
                </h1>

                <p className="text-body agreement-rules-desc">
                    {gameConfig ? (
                        <>
                            interest rate = {gameConfig.interestPct}%, starting budget = {gameConfig.initBudget}, play rounds = {gameConfig.maxTurns},
                            <br />
                            max spawns = {gameConfig.maxSpawns}, spawn cost = {gameConfig.spawnCost}, hex cost = {gameConfig.hexPurchaseCost}
                        </>
                    ) : (
                        "Loading..."
                    )}
                </p>

                <h2 className="text-subheading agreement-minion-title">
                    Minions Type(s)
                </h2>

                <div className="agreement-minion-list">
                    {(displayMinions as any[]).map((minion: any, index: number) => {
                        const avatarSrc = (MinionsIMG as any)[minion.type || minion.name]

                        return (
                            <div 
                                key={index} 
                                className="agreement-minion-card"
                                onClick={() => isDuel && !isHost && minion.strategy && handleViewStrategy(minion)}
                                style={{ cursor: isDuel && !isHost ? "pointer" : "default" }}
                            >
                                <img
                                    src={avatarSrc}
                                    alt={minion.customName || minion.name}
                                    className="agreement-minion-img"
                                />
                                <h4 className="text-body agreement-minion-name">
                                    {minion.customName || minion.name}
                                </h4>
                                <p className="text-body agreement-minion-def">
                                    DEF: {minion.def || minion.defense}
                                </p>
                                {isDuel && !isHost && minion.strategy && (
                                    <p className="text-body" style={{ fontSize: "10px", color: "#888" }}>
                                        Click to view strategy
                                    </p>
                                )}
                            </div>
                        )
                    })}
                </div>

                <div className="agreement-actions">
                    {(!isDuel || !isHost) && (
                        <button
                            onClick={handleDisagree}
                            className="agreement-btn agreement-btn-disagree"
                        >
                            disagree
                        </button>
                    )}

                    <button
                        onClick={handleAgree}
                        className="agreement-btn agreement-btn-agree"
                        disabled={loading}
                    >
                        {loading ? "Loading..." : "agree"}
                    </button>
                </div>

            </div>
        </div>
        </CenteredLayout>

            <Modal isOpen={showStrategyModal} onClose={() => setShowStrategyModal(false)}>
                <h2 className="strategy-modal-title">{selectedStrategy?.name} Strategy</h2>
                <pre className="strategy-display-box">
            {selectedStrategy?.strategy}
        </pre>
            </Modal>
        </AppLayout>
    )
}