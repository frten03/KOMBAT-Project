import { useState } from "react"
import { useNavigate } from "react-router-dom"
import AppLayout from "../layouts/AppLayout"
import CenteredLayout from "../layouts/CenteredLayout"
import BackButton from "../components/BackButton"
import "../styles/join.css"

export default function JoinRoomPage() {
    const [roomId, setRoomId] = useState("")
    const navigate = useNavigate()

    const handleJoin = () => {
        if (!roomId) return
        navigate(`/waiting?roomId=${roomId}&role=guest`)
    }


    return (
        <AppLayout>
            <CenteredLayout>
                <div className="join-wrapper">

                    <BackButton className="join-back" />

                    <div className="join-content">
                        <h1 className="join-title">Join Room</h1>

                        <input
                            className="join-input"
                            type="text"
                            inputMode="numeric"
                            pattern="[0-9]*"
                            value={roomId}
                            onChange={(e) => {
                                const onlyNumbers = e.target.value
                                    .replace(/\D/g, "")
                                    .slice(0, 4)
                                setRoomId(onlyNumbers)
                            }}
                            placeholder="Enter Room ID"
                        />

                        <button
                            className="btn room join-btn"
                            onClick={handleJoin}
                        >
                            Join
                        </button>
                    </div>

                </div>
            </CenteredLayout>
        </AppLayout>
    )
}