import { useNavigate } from "react-router-dom"
import AppLayout from "../layouts/AppLayout"
import CenteredLayout from "../layouts/CenteredLayout"
import "../styles/globals.css"
import "../styles/start.css"

export default function StartPage() {
    const navigate = useNavigate()

    const handleSelectMode = (mode: string) => {

        localStorage.removeItem("duelHostConfig")
        localStorage.removeItem("duelReadyState")

        if (mode === "duel") {
            navigate("/room")
        } else {
            navigate(`/setup?mode=${mode}`)
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
                        >
                            auto
                        </button>

                        <button
                            className="btn solitaire"
                            onClick={() => handleSelectMode("solitaire")}
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