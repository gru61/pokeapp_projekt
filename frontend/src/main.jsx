import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './styles/global.css';
import './styles/shared.css';

/**
 * @file main.jsx
 * @description
 * Einstiegspunkt der Anwendung (Entry Point).
 *
 * Bindet die Haupt-App-Komponente (`App`) in das HTML-Element mit der ID "root" ein.
 * Initialisiert damit das gesamte Frontend und sorgt dafür, dass alle Routen und Komponenten
 * korrekt im Browser angezeigt werden.
 *
 * Besonderheiten:
 * - Verwendet `React.StrictMode` für zusätzliche Entwicklungs-Checks und Warnungen.
 * - Bindet globale CSS-Dateien für das Styling der gesamten Anwendung ein.
 *
 * Typische Verwendung:
 * - Wird vom Build-Tool (z.B. Vite, Webpack) als Hauptstartpunkt für das Frontend erkannt.
 *
 * @example
 * // Muss in der HTML-Datei ein <div id="root"></div> vorhanden sein!
 */

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);
