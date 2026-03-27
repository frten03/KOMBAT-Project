import { useNavigate } from "react-router-dom"
import { useState } from "react"
import AppLayout from "../layouts/AppLayout"
import "../styles/room.css"
import BackButton from "../components/BackButton"
import { createGame } from "../game/gameApi"

export default function RoomPage() {
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleCreate = async () => {
        setLoading(true)
        setError(null)
        
        try {
            const session = await createGame("duel")
            navigate(`/waiting?gameId=${session.gameId}&role=host`)
        } catch (err) {
            setError(String(err))
        } finally {
            setLoading(false)
        }
    }

    const handleJoin = () => {
        navigate("/join")
    }
    
    return (
        <AppLayout>
            <div className="room-page">

                <BackButton className="room-back" />

                <div className="room-center">
                    {error && <p style={{ color: "red" }}>{error}</p>}
                    <button 
                        className="btn room" 
                        onClick={handleCreate}
                        disabled={loading}
                    >
                        {loading ? "Creating..." : "create room"}
                    </button>

                    <p className="text-body">Or</p>

                    <button className="btn join" onClick={handleJoin}>
                        join room
                    </button>
                </div>

            </div>
        </AppLayout>
    )
}