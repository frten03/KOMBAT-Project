import { useNavigate } from "react-router-dom"
import "../styles/endgame.css"
import AppLayout from "../layouts/AppLayout.tsx";
import CenteredLayout from "../layouts/CenteredLayout.tsx";
import homeIcon from "../assets/icons/home.png"
import "../styles/homebtn.css"


export default function EndGamePage() {
    const navigate = useNavigate()


    return (
        <AppLayout><CenteredLayout>
        <div className="endgame-page">
            <h1 className="endgame-title">Game Over</h1>
            <h2 className="endgame-winner">Winner: Player 1</h2>

            <button
                className="home-btn"
                onClick={() => navigate("/", { replace: true })}
            >
                <img src={homeIcon} alt="home" />
            </button>
        </div>
        </CenteredLayout></AppLayout>
    )
}