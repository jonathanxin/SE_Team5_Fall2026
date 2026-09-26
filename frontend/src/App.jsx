import { useEffect, useState } from 'react'
import './App.css'

function App() {
  const [screen, setScreen] = useState("splash");
  // Show splash screen for 3 seconds
  useEffect(() => { 
    const timer = setTimeout(() => {
      setScreen("player-entry");
    }, 3000); 
    return () => clearTimeout(timer);
  }, []);
  if (screen === "splash") { 
    return (
      <div className="splash-screen">
        <img src="/logo.jpg" alt="Photon Laser Tag" />
      </div>
    );
  }
  return <PlayerEntryScreen />;
}

function PlayerEntryScreen() {
}


export default App
