import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import AppLayout from "../layouts/AppLayout"
import CenteredLayout from "../layouts/CenteredLayout"
import BackButton from "../components/BackButton"
import "../styles/join.css"
import { joinGame } from "../game/gameApi"

export default function JoinRoomPage() {
    const [gameId, setGameId] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()
    const [buttonClicked, setButtonClicked] = useState(false)
    
    useEffect(() => {
        console.log("[JoinRoom] Component mounted, resetting state")
        setGameId("")
        setLoading(false)
        setButtonClicked(false)
    }, [])
    
    console.log("[JoinRoom] Component rendered, gameId:", gameId, "loading:", loading, "buttonClicked:", buttonClicked)

    const handleJoin = async () => {
        console.log("[JoinRoom] handleJoin called, gameId:", gameId, "buttonClicked:", buttonClicked)
        if (!gameId || !buttonClicked) {
            console.log("[JoinRoom] No gameId or button not clicked, returning early")
            return
        }
        
        setLoading(true)
        setError(null)
        
        try {
            console.log("[JoinRoom] Calling joinGame API...")
            const result = await joinGame(gameId)
            console.log("[JoinRoom] joinGame succeeded, result:", result, "navigating...")
            navigate(`/waiting?gameId=${gameId}&role=guest`)
        } catch (err) {
            console.error("[JoinRoom] joinGame failed:", err)
            setError(String(err))
            setLoading(false)
            setButtonClicked(false)
        }
    }


    return (
        <AppLayout>
            <CenteredLayout>
                <div className="join-wrapper">

                    <BackButton className="join-back" />

                    <div className="join-content">
                        <h1 className="join-title">Join Room</h1>
                        
                        {error && <p style={{ color: "red" }}>{error}</p>}

                        <input
                            className="join-input"
                            type="text"
                            value={gameId}
                            onChange={(e) => {
                                console.log("[JoinRoom] Input changed:", e.target.value)
                                setGameId(e.target.value.slice(0, 6))
                            }}
                            placeholder="Enter Game ID"
                        />

                        <button
                            type="button"
                            className="btn room join-btn"
                            onClick={() => {
                                console.log("[JoinRoom] Button clicked, gameId:", gameId)
                                setButtonClicked(true)
                                handleJoin()
                            }}
                            disabled={loading || !gameId}
                        >
                            {loading ? "Joining..." : "Join"}
                        </button>
                    </div>

                </div>
            </CenteredLayout>
        </AppLayout>
    )
}