import { useNavigate } from "react-router-dom"
import backIcon from "../assets/icons/back.png"
import "../styles/BackButton.css"

interface BackButtonProps {
    className?: string
}

export default function BackButton({ className = "" }: BackButtonProps) {
    const navigate = useNavigate()

    return (
        <button
            className={`back-btn ${className}`}
            onClick={() => navigate(-1)}
        >
            <img src={backIcon} alt="back" />
        </button>
    )
}