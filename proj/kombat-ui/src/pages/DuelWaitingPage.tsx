import { useSearchParams, useNavigate } from "react-router-dom"
import { useEffect, useRef } from "react"
import CenteredLayout from "../layouts/CenteredLayout"
import AppLayout from "../layouts/AppLayout"
import "../styles/waiting.css"
import BackButton from "../components/BackButton"
import { getRoomStatus } from "../game/gameApi"

export default function DuelWaitingPage() {
    const [searchParams] = useSearchParams()
    const navigate = useNavigate()

    const gameId = searchParams.get("gameId")
    const role = searchParams.get("role")
    const isHost = role === "host"
    
    console.log("[DuelWaiting] Mounted with:", { gameId, role, isHost })

    const lastConfigSubmittedRef = useRef(false)
    const hasNavigatedRef = useRef(false)

    useEffect(() => {
        if (!gameId || hasNavigatedRef.current) return

        const pollInterval = setInterval(async () => {
            if (hasNavigatedRef.current) return
            
            try {
                const status = await getRoomStatus(gameId)
                console.log("[DuelWaiting] Poll:", { isHost, status })
                
                if (isHost) {
                    console.log("[DuelWaiting] Host check:", {
                        allAgreed: status.guestJoined && status.hostAgreed && status.guestAgreed,
                        configReady: status.guestJoined && status.configSubmitted,
                        justJoined: status.guestJoined
                    })
                    
                    if (status.guestJoined && status.hostAgreed && status.guestAgreed) {
                        console.log("[DuelWaiting] Navigating to gameplay")
                        hasNavigatedRef.current = true
                        navigate(`/gameplay?mode=duel&role=host&gameId=${gameId}`)
                    } else if (status.guestJoined && status.configSubmitted) {
                        console.log("[DuelWaiting] Navigating to agreement")
                        hasNavigatedRef.current = true
                        navigate(`/agreement?mode=duel&role=host&gameId=${gameId}`)
                    } else if (status.guestJoined) {
                        console.log("[DuelWaiting] Navigating to setup")
                        hasNavigatedRef.current = true
                        navigate(`/setup?mode=duel&role=host&gameId=${gameId}`)
                    }
                    if (lastConfigSubmittedRef.current && !status.configSubmitted) {
                        lastConfigSubmittedRef.current = false
                        hasNavigatedRef.current = true
                        navigate(`/setup?mode=duel&role=host&gameId=${gameId}`)
                    }
                } else {
                    if (status.configSubmitted && status.hostAgreed && status.guestAgreed) {
                        hasNavigatedRef.current = true
                        navigate(`/gameplay?mode=duel&role=guest&gameId=${gameId}`)
                    } else if (status.configSubmitted) {
                        hasNavigatedRef.current = true
                        navigate(`/agreement?mode=duel&role=guest&gameId=${gameId}`)
                    }
                }
            } catch (err) {
                console.error("Polling error:", err)
            }
        }, 1000)

        return () => clearInterval(pollInterval)
    }, [gameId, navigate, isHost])

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
                            Game ID : {gameId}
                        </h1>

                    </div>

                    <p className="waiting-text">
                        {isHost
                            ? "Waiting for the other player to join"
                            : "Waiting for host to configure"}
                        <span className="dots">
                            <span>.</span><span>.</span><span>.</span>
                        </span>
                    </p>

                </div>
            </CenteredLayout>
        </AppLayout>
    )
}