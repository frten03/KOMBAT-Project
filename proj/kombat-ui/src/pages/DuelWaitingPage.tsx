import { useSearchParams } from "react-router-dom"
import CenteredLayout from "../layouts/CenteredLayout"
import AppLayout from "../layouts/AppLayout.tsx";
import "../styles/waiting.css"
import BackButton from "../components/BackButton.tsx";

export default function DuelWaitingPage() {
    const [searchParams] = useSearchParams()

    const roomId = searchParams.get("roomId")
    const role = searchParams.get("role")

    const isHost = role === "host"

    return (
        <AppLayout>
            <CenteredLayout>
                <div className="waiting-wrapper">

                    <BackButton className="waiting-back" />

                    <div className="waiting-content">

                        <div className="waiting-badge">
                            {isHost ? "Room Created !" : "Join Successfully !"}
                        </div>

                        <h1 className="waiting-room">
                            Room ID : {roomId}
                        </h1>

                    </div>

                    <p className="waiting-text">
                        {isHost
                            ? "Waiting for the other player to join"
                            : "Waiting for host configuration"}
                        <span className="dots">
        <span>.</span><span>.</span><span>.</span>
      </span>
                    </p>

                </div>
            </CenteredLayout>
        </AppLayout>
    )
}