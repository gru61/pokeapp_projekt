import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Pokedex from './pages/Pokedex';
import OwnedPokemon from './pages/OwnedPokemon';
import BoxView from './pages/BoxView';
import './App.css';


function App() {

  return (
    <Router>
      <div className="app">
        <main className={"main-content"}>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/pokedex" element={<Pokedex />} />
                <Route path="/owned" element={<OwnedPokemon />} />
                <Route path="/box-view" element={<BoxView />} />
            </Routes>
        </main>
      </div>
    </Router>
  );
}

function Home() {
  return (
    <div className="home">
        <div className="menu-cards">
            <div className="menu-card" onClick={() => window.location.href = "/pokedex"}>
                <h2>Pokédex</h2>
                <p>Alle 151 OG's</p>
            </div>
            <div className="menu-card" onClick={() => window.location.href = "/owned"}>
                <h2>Pokémon in Besitz</h2>
                <p>Deine OG's</p>
            </div>
            <div className="menu-card" onClick={() => window.location.href = "/box-view"}>
                <h2>Box View</h2>
                <p>Die Boxen</p>
            </div>
        </div>
    </div>
  );
}

export default App;