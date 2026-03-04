import { useNavigate } from "react-router-dom"
import AppLayout from "../layouts/AppLayout"
import "../styles/room.css"
import BackButton from "../components/BackButton";

export default function RoomPage() {
    const navigate = useNavigate()

    const handleCreate = () => {
        const roomId = Math.floor(Math.random() * 9000) + 1000
        navigate(`/waiting?roomId=${roomId}&role=host`)
    }

    const handleJoin = () => {
        navigate("/join")
    }
    return (
        <AppLayout>
            <div className="room-page">

                <BackButton className="room-back" />

                <div className="room-center">
                    <button className="btn room" onClick={handleCreate}>
                        create room
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