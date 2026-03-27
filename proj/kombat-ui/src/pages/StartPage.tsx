import { useNavigate } from "react-router-dom"
import { useState } from "react"
import AppLayout from "../layouts/AppLayout"
import CenteredLayout from "../layouts/CenteredLayout"
import "../styles/globals.css"
import "../styles/start.css"
import { createGame } from "../game/gameApi"

export default function StartPage() {
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)

    const handleSelectMode = async (mode: string) => {
        localStorage.removeItem("duelHostConfig")
        localStorage.removeItem("duelReadyState")

        if (mode === "duel") {
            navigate("/room")
        } else {
            setLoading(true)
            try {
                const session = await createGame(mode.toLowerCase())
                navigate(`/setup?mode=${mode}&gameId=${session.gameId}`)
            } catch (err) {
                console.error("Failed to create game:", err)
            } finally {
                setLoading(false)
            }
        }
    }

    return (
        <AppLayout>
            <CenteredLayout>
                <div className="start-container">
                    <h1 className="text-title">KOMBAT</h1>
                    <p className="text-subheading">select mode</p>

                    <div className="button-group">
                        <button
                            className="btn auto"
                            onClick={() => handleSelectMode("auto")}
                            disabled={loading}
                        >
                            {loading ? "Loading..." : "auto"}
                        </button>

                        <button
                            className="btn solitaire"
                            onClick={() => handleSelectMode("solitaire")}
                            disabled={loading}
                        >
                            solitaire
                        </button>

                        <button
                            className="btn duel"
                            onClick={() => handleSelectMode("duel")}
                        >
                            duel
                        </button>
                    </div>
                </div>
            </CenteredLayout>
        </AppLayout>
    )
}