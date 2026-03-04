import { useLocation, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"

import tankImg from "../assets/minions/tank.png"
import archerImg from "../assets/minions/archer.png"
import warriorImg from "../assets/minions/warrior.png"
import pouImg from "../assets/minions/pou.png"
import dogImg from "../assets/minions/dog.jpg"

import "../styles/agreement.css"
import AppLayout from "../layouts/AppLayout.tsx";
import CenteredLayout from "../layouts/CenteredLayout.tsx";


type MinionConfig = {
    type: string
    customName: string
    def: number
    strategy: string
    isValid: boolean
}


const IMAGE_MAP: Record<string, string> = {
    Tank: tankImg,
    Archer: archerImg,
    Warrior: warriorImg,
    Pou: pouImg,
    Dog: dogImg,
}

export default function AgreementPage() {
    const location = useLocation()
    const navigate = useNavigate()

    const params = new URLSearchParams(location.search)
    const mode = params.get("mode")
    const role = params.get("role")

    const [selectedMinions, setSelectedMinions] =
        useState<MinionConfig[]>([])


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


    const handleAgree = () => {
        navigate(`/gameplay?mode=${mode}&role=${role}`, {
            state: { selectedMinions }
        })
    }

    const handleDisagree = () => {
        localStorage.removeItem("agreementMinions")

        // Solitaire + Auto
        if (mode === "solitaire" || mode === "auto") {
            navigate(`/setup?mode=${mode}`)
            return
        }

        // Duel
        if (mode === "duel") {

            if (role === "host") return

            if (role === "guest") {
                navigate(`/waiting?mode=duel&role=guest`)
            }
        }
    }

    console.log("Agreement selectedMinions:", selectedMinions)

    return (
        <AppLayout><CenteredLayout><div className="agreement-page">
            <div className="agreement-content">

                <h1 className="text-heading agreement-rules-title">
                    Game Rules
                </h1>

                <p className="text-body agreement-rules-desc">
                    interest rate = 5%, starting budget = 5000, play rounds = 25,
                    <br />
                    max spawns = 30, spawn cost = 100, hex cost = 100
                </p>

                <h2 className="text-subheading agreement-minion-title">
                    Minions Type(s)
                </h2>

                <div className="agreement-minion-list">
                    {selectedMinions.map((minion, index) => (
                        <div key={index} className="agreement-minion-card">
                            <img
                                src={IMAGE_MAP[minion.type]}
                                alt={minion.customName}
                                className="agreement-minion-img"
                            />
                            <h4 className="text-body agreement-minion-name">
                                {minion.customName}
                            </h4>
                            <p className="text-body agreement-minion-def">
                                DEF: {minion.def}
                            </p>
                        </div>
                    ))}
                </div>

                <div className="agreement-actions">
                    {(mode !== "duel" || role === "guest") && (
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
                    >
                        agree
                    </button>
                </div>

            </div>
        </div>
        </CenteredLayout></AppLayout>
    )
}