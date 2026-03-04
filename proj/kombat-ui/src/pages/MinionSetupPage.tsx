import { useNavigate, useLocation } from "react-router-dom"
import { useState } from "react"
import "../styles/minion-setup.css"
import AppLayout from "../layouts/AppLayout"

import tankImg from "../assets/minions/tank.png"
import archerImg from "../assets/minions/archer.png"
import warriorImg from "../assets/minions/warrior.png"
import pouImg from "../assets/minions/pou.png"
import dogImg from "../assets/minions/dog.jpg"

import validIcon from "../assets/icons/valid.png"
import invalidIcon from "../assets/icons/invalid.png"

import BackButton from "../components/BackButton"


type MinionType = {
    type: string
    def: number
    avatar: string
}

type MinionConfig = {
    type: string
    customName: string
    def: number
    strategy: string
    isValid: boolean
}


const DEFAULT_STRATEGY = `if (opponent) {
  shoot up 2;
  move up;
} else {
  move upleft;
}`

const MINIONS: MinionType[] = [
    { type: "Tank", def: 200, avatar: tankImg },
    { type: "Archer", def: 80, avatar: archerImg },
    { type: "Warrior", def: 120, avatar: warriorImg },
    { type: "Pou", def: 100, avatar: pouImg },
    { type: "Dog", def: 90, avatar: dogImg },
]

/* ================= COMPONENT ================= */

export default function MinionSetupPage() {

    const navigate = useNavigate()
    const location = useLocation()

    const params = new URLSearchParams(location.search)
    const mode = params.get("mode")

    const [current, setCurrent] = useState<MinionType>(MINIONS[0])

    const [editing, setEditing] = useState<MinionConfig>({
        type: MINIONS[0].type,
        customName: MINIONS[0].type,
        def: MINIONS[0].def,
        strategy: DEFAULT_STRATEGY,
        isValid: true,
    })

    const [savedConfigs, setSavedConfigs] =
        useState<Record<string, MinionConfig>>({})

    const [selected, setSelected] = useState<string[]>([])


    const handleViewMinion = (minion: MinionType) => {
        setCurrent(minion)

        const saved = savedConfigs[minion.type]

        if (saved) {
            setEditing(saved)
        } else {
            setEditing({
                type: minion.type,
                customName: minion.type,
                def: minion.def,
                strategy: DEFAULT_STRATEGY,
                isValid: true,
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


    const handleValidate = () => {

        const isValid =
            editing.strategy.includes("move") ||
            editing.strategy.includes("shoot")

        const updated = { ...editing, isValid }

        setEditing(updated)

        setSavedConfigs(prev => ({
            ...prev,
            [editing.type]: updated
        }))
    }

    const hasInvalidSelected = (() => {

        if (selected.length === 0) return true

        return selected.some(type => {

            if (type === editing.type) {
                return !editing.isValid
            }

            const saved = savedConfigs[type]
            return saved ? !saved.isValid : false
        })

    })()


    const handleConfirm = () => {

        if (hasInvalidSelected) return

        const finalConfigs = selected.map(type => {

            if (type === editing.type) {
                return editing
            }

            return savedConfigs[type] ?? {
                type,
                customName: type,
                def: MINIONS.find(m => m.type === type)!.def,
                strategy: DEFAULT_STRATEGY,
                isValid: true,
            }
        })

        navigate(`/agreement?mode=${mode}`, {
            state: { selectedMinions: finalConfigs }
        })
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

                        {MINIONS.map(minion => {

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
                                            current.type === minion.type
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
                                            const englishOnly = e.target.value.replace(/[^a-zA-Z ]/g, "")

                                            setEditing(prev => ({
                                                ...prev,
                                                customName: englishOnly
                                            }))
                                        }}
                                        onKeyDown={(e) => {
                                            if (e.key === "Enter") {
                                                e.preventDefault()

                                                setSavedConfigs(prev => ({
                                                    ...prev,
                                                    [editing.type]: editing
                                                }))
                                            }
                                        }}
                                    />
                                </div>

                                <div className="row-field">
                                    <label>DEF :</label>
                                    <input
                                        type="number"
                                        value={editing.def}
                                        onChange={(e) =>
                                            setEditing(prev => ({
                                                ...prev,
                                                def: Number(e.target.value)
                                            }))
                                        }
                                    />
                                </div>

                            </div>

                            <div className="avatar-wrapper">
                                <img
                                    src={current.avatar}
                                    alt={current.type}
                                />
                            </div>

                        </div>

                        <div className="strategy-section">

                            <div className="strategy-header">
                                <h3 className="section-title">
                                    Strategy Script
                                </h3>

                                <img
                                    src={
                                        editing.isValid
                                            ? validIcon
                                            : invalidIcon
                                    }
                                    alt="validate"
                                    className="validate-icon"
                                    onClick={handleValidate}
                                />
                            </div>

                            <textarea
                                value={editing.strategy}
                                onChange={(e) =>
                                    setEditing(prev => ({
                                        ...prev,
                                        strategy: e.target.value,
                                        isValid: false
                                    }))
                                }
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