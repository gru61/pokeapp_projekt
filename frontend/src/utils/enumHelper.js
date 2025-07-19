/**
 * @file enumHelpers.js
 * @description
 * Utility-Funktionen für das Mapping zwischen UI-Anzeige-Strings (z.B. "Grün", "Box 3")
 * und den von der Backend-API erwarteten Enum-Werten (z.B. "GRUEN", "BOX3").
 *
 * Wird in allen Komponenten verwendet, die Daten zwischen Frontend-UI und API konvertieren müssen.
 *
 * Beispiel-Anwendungsfälle:
 * - Konvertierung von Editionen und Boxnamen beim Senden von Requests
 * - Anzeige und Auswahl von Boxen und Editionen im Frontend
 */

/**
 * Wandelt einen UI-Enum-String (z.B. Edition, BoxName) in den API-kompatiblen Enum-Wert um.
 * Unterstützt Umlaute und Leerzeichen-Konvertierung.
 *
 * @param {string} val - Eingabewert (z.B. "Grün" oder "Box 3")
 * @returns {string} API-kompatibler Enum-Value (z.B. "GRUEN" oder "BOX3")
 */
export function apiEnum(val) {
    if (!val) return "";
    return val
        .toUpperCase()
        .replace("Ü", "UE")
        .replace("Ö", "OE")
        .replace("Ä", "AE")
        .replace("ß", "SS");
}

/**
 * Wandelt den angezeigten Boxnamen in den API-Enum-Namen um (z.B. "Box 3" → "BOX3").
 *
 * @param {string} name - Angezeigter Boxname im UI (z.B. "Team", "Box 1")
 * @returns {string} Enum-Wert für die API (z.B. "TEAM", "BOX1")
 */
export function apiBoxName(name) {
    if (!name) return "";
    if (name === "Team") return "TEAM";
    const boxMatch = name.match(/^Box (\d{1,2})$/);
    if (boxMatch) {
        return "BOX" + boxMatch[1];
    }
    // Fallback: alles groß, Leerzeichen raus
    return name.toUpperCase().replace(/\s+/g, "");
}
