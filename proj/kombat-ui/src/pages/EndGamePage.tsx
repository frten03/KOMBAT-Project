import { useNavigate } from "react-router-dom"
import "../styles/endgame.css"
import AppLayout from "../layouts/AppLayout.tsx"
import CenteredLayout from "../layouts/CenteredLayout.tsx"
import homeIcon from "../assets/icons/home.png"
import "../styles/homebtn.css"
import { useGameStore } from "../store/gameStore"

export default function EndGamePage() {
    const navigate = useNavigate()
    const { gameOverReason, winner, reset } = useGameStore()

    const handleGoHome = () => {
        reset()
        navigate("/", { replace: true })
    }

    return (
        <AppLayout><CenteredLayout>
        <div className="endgame-page">
            <h1 className="endgame-title">Game Over</h1>
            <h2 className="endgame-winner">Winner: {winner || "Unknown"}</h2>
            {gameOverReason && (
                <p className="endgame-reason">{gameOverReason}</p>
            )}

            <button
                className="home-btn"
                onClick={handleGoHome}
            >
                <img src={(homeIcon) as string} alt="home" />
            </button>
        </div>
        </CenteredLayout></AppLayout>
    )
}