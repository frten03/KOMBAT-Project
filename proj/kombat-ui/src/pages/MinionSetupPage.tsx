import { useNavigate, useLocation } from "react-router-dom"
import { useState, useEffect, useRef } from "react"
import "../styles/minion-setup.css"
import AppLayout from "../layouts/AppLayout"

import { MinionsIMG } from "../assets";

import validIcon from "../assets/icons/valid.png"
import invalidIcon from "../assets/icons/invalid.png"
import BackButton from "../components/BackButton"

import type { MinionConfig } from "../types/gameTypes.ts"
import { validateStrategy, getGameState } from "../game/gameApi.tsx"


export default function MinionSetupPage() {

    const navigate = useNavigate()
    const location = useLocation()

    const params = new URLSearchParams(location.search)
    const mode = params.get("mode")
    const gameId = params.get("gameId")
    const role = params.get("role")

    const [minionTypes, setMinionTypes] = useState<MinionConfig[]>([])
    const [current, setCurrent] = useState<MinionConfig | null>(null)

    const [editing, setEditing] = useState<MinionConfig | null>(null)

    const [savedConfigs, setSavedConfigs] =
        useState<Record<string, MinionConfig>>({})

    const [selected, setSelected] = useState<string[]>([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchMinionTypes = async () => {
            try {
                if (gameId) {
                    const state = await getGameState(gameId)
                    const types = state.availableMinionTypes.map(t => ({
                        type: t.name,
                        customName: t.name,
                        def: t.defense,
                        strategy: t.strategy,
                        isValid: true,
                        avatar: (MinionsIMG as any)[t.name] || ""
                    }))
                    setMinionTypes(types)
                    if (types.length > 0) {
                        setCurrent(types[0])
                        setEditing({ ...types[0] })
                    }
                }
            } catch (err) {
                console.error("Failed to fetch minion types:", err)
            } finally {
                setLoading(false)
            }
        }
        fetchMinionTypes()
    }, [gameId])


    const handleViewMinion = (minion: MinionConfig) => {
        setCurrent(minion)

        const saved = savedConfigs[minion.type]

        if (saved) {
            setEditing(saved)
        } else {
            setEditing({
                type: minion.type,
                customName: minion.type,
                def: minion.def,
                strategy: minion.strategy,
                isValid: true,
                avatar: (MinionsIMG as any)[minion.type],
            })
        }
    }

    const toggleSelect = (type: string) => {
        setSelected(prev =>
            prev.includes(type)
                ? prev.filter(t => t !== type)
                : [...prev, type]
        )
    }


    const [validating, setValidating] = useState(false)
    const [lastValidatedStrategy, setLastValidatedStrategy] = useState(editing?.strategy || "")
    const validateTimeoutRef = useRef<number | null>(null);

    const handleValidate = async () => {
        if (!editing || editing.strategy === lastValidatedStrategy) return
        setLastValidatedStrategy(editing.strategy)
        setValidating(true)
        try {
            const result = await validateStrategy(editing.strategy)
            const updated = { ...editing, isValid: result.valid }
            setEditing(updated)
            setSavedConfigs(prev => ({
                ...prev,
                [editing.type]: updated
            }))
        } catch (err) {
            const updated = { ...editing, isValid: false }
            setEditing(updated)
            setSavedConfigs(prev => ({
                ...prev,
                [editing.type]: updated
            }))
        } finally {
            setValidating(false)
        }
    }

    useEffect(() => {
        if (!editing) return
        if (validateTimeoutRef.current) {
            clearTimeout(validateTimeoutRef.current)
        }
        validateTimeoutRef.current = setTimeout(() => {
            handleValidate()
        }, 500)
        return () => {
            if (validateTimeoutRef.current) {
                clearTimeout(validateTimeoutRef.current)
            }
        }
    }, [editing?.strategy])

    const hasInvalidSelected = (() => {
        if (selected.length === 0 || !editing) return true

        return selected.some(type => {
            if (type === editing.type) {
                return !editing.isValid
            }

            const saved = savedConfigs[type]
            return saved ? !saved.isValid : false
        })
    })()


    const handleConfirm = async () => {
        if (hasInvalidSelected || !editing) return

        const finalConfigs = selected.map(type => {
            const base = minionTypes.find(m => m.type === type)!
            if (type === editing.type) {
                return {
                    ...editing,
                    avatar: base.avatar
                }
            }

            const saved = savedConfigs[type]

            return saved
                ? { ...saved, avatar: base.avatar }
                : {
                    type,
                    customName: type,
                    def: base.def,
                    strategy: base.strategy,
                    isValid: true,
                    avatar: base.avatar
                }
        })

        if (mode === "duel" && role === "host") {
            try {
                const { submitConfig } = await import("../game/gameApi")
                await submitConfig(gameId!, selected)
                navigate(`/waiting?gameId=${gameId}&role=host`)
            } catch (err) {
                console.error("Failed to submit config:", err)
                navigate(`/waiting?gameId=${gameId}&role=host`)
            }
        } else {
            navigate(`/agreement?mode=${mode}&gameId=${gameId}`, {
                state: { selectedMinions: finalConfigs }
            })
        }
    }

    if (loading) {
        return (
            <AppLayout>
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
                    <p>Loading minion types...</p>
                </div>
            </AppLayout>
        )
    }

    if (!current || !editing) {
        return (
            <AppLayout>
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
                    <p>No minion types available</p>
                </div>
            </AppLayout>
        )
    }

    return (
        <AppLayout>
            <div className="setup-container">

                <div className="setup-header">
                    <h1 className="text-subheading">
                        Setup – Configure Minions
                    </h1>
                    <h2 className="text-body">
                        Game Mode: {mode}
                    </h2>
                </div>

                <div className="setup-body">

                    <div className="left-panel">
                        <h3 className="section-title">Minion Types</h3>

                        {minionTypes.map(minion => {

                            const saved = savedConfigs[minion.type]
                            const displayName =
                                saved?.customName || minion.type

                            return (
                                <div key={minion.type} className="checkbox-item">

                                    <input
                                        type="checkbox"
                                        checked={selected.includes(minion.type)}
                                        onChange={() =>
                                            toggleSelect(minion.type)
                                        }
                                    />

                                    <span
                                        onClick={() => handleViewMinion(minion)}
                                        className={`minion-name ${
                                            current?.type === minion.type
                                                ? "active-minion"
                                                : ""
                                        }`}
                                    >
                                        {displayName}
                                    </span>

                                </div>
                            )
                        })}
                    </div>

                    <div className="right-panel">

                        <div className="minion-info">

                            <div className="info-left">

                                <div className="row-field">
                                    <label>Name   :</label>
                                    <input
                                        value={editing.customName}
                                        placeholder="Enter name"
                                        onChange={(e) => {
                                            const englishOnly = e.target.value.replace(/[^a-zA-Z ]/g, "");
                                            setEditing(prev => {
                                                if (!prev) return null;
                                                return {
                                                    ...prev,
                                                    customName: englishOnly
                                                };
                                            });
                                        }}
                                        onKeyDown={(e) => {
                                            if (e.key === "Enter") {
                                                e.preventDefault();
                                                if (!editing) return;

                                                setSavedConfigs(prev => {
                                                    if (!prev) return prev;
                                                    return {
                                                        ...prev,
                                                        [editing.type]: editing
                                                    };
                                                });
                                            }
                                        }}
                                    />
                                </div>

                                <div className="row-field">
                                    <label>DEF :</label>
                                    <input
                                        type="number"
                                        value={editing?.def ?? 0}
                                        onChange={(e) => {
                                            const val = e.target.value === "" ? 0 : Number(e.target.value);
                                            setEditing(prev => prev ? {
                                                ...prev,
                                                def: val
                                            } : null);
                                        }}
                                    />
                                </div>

                            </div>

                            <div className="avatar-wrapper">
                                <img
                                    src={editing.avatar}
                                    alt={editing.type}
                                />
                            </div>

                        </div>

                        <div className="strategy-section">

                                <div className="strategy-header">
                                <h3 className="section-title">
                                    Strategy Script
                                </h3>

                                <div 
                                    className="validate-icon-wrapper"
                                    style={{ opacity: validating ? 0.5 : 1 }}
                                >
                                    <img
                                        src={validIcon as string}
                                        alt="valid"
                                        className="validate-icon"
                                        style={{ opacity: (validating || editing.isValid) ? 1 : 0 }}
                                    />
                                    <img
                                        src={invalidIcon as string}
                                        alt="invalid"
                                        className="validate-icon"
                                        style={{ opacity: (!validating && !editing.isValid) ? 1 : 0 }}
                                    />
                                </div>
                            </div>

                            <textarea
                                value={editing?.strategy || ""}
                                onChange={(e) => {
                                    const newValue = e.target.value;
                                    setEditing(prev => prev ? {
                                        ...prev,
                                        strategy: newValue
                                    } : null);
                                }}
                            />

                        </div>

                    </div>
                </div>

                <button
                    className="confirm-btn"
                    onClick={handleConfirm}
                    disabled={hasInvalidSelected}
                >
                    Confirm
                </button>
                <BackButton className="back-fixed" />
            </div>
        </AppLayout>
    )
}