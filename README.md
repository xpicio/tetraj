# Tetraj

Tetraj è una riproduzione del videogioco Tetris sviluppato durante il corso di Programmazione ad Oggetti. Università di Bologna, Corso di Laurea in Ingegneria e Scienze Informatiche. Anno accademico 2024/2025.

## Come giocare

### Stati del gioco

Il gioco ha 4 stati principali:

| Stato           | Descrizione          |
| --------------- | -------------------- |
| **MENU**        | Schermata principale |
| **PLAYING**     | Partita in corso     |
| **GAME_OVER**   | Fine partita         |
| **LEADERBOARD** | Classifica punteggi  |

### Controlli

#### Menu principale

| Azione      | Tasto   |
| ----------- | ------- |
| Avvia gioco | `ENTER` |
| Leaderboard | `L`     |
| Esci        | `ESC`   |

#### Durante la partita

| Azione                         | Tasti              |
| ------------------------------ | ------------------ |
| Muovi a sinistra               | `←` oppure `A`     |
| Muovi a destra                 | `→` oppure `D`     |
| Soft drop (caduta accelerata)  | `↓` oppure `S`     |
| Hard drop (caduta immediata)   | `SPACE`            |
| Rotazione oraria               | `↑` oppure `W`     |
| Rotazione antioraria           | `CTRL` oppure `Z`  |
| Hold (metti da parte il pezzo) | `SHIFT` oppure `C` |
| Pausa                          | `P`                |
| Torna al menu                  | `ESC`              |

Durante la pausa: `P` o `ESC` per riprendere.

#### Game Over

| Azione        | Tasto   |
| ------------- | ------- |
| Rigioca       | `ENTER` |
| Leaderboard   | `L`     |
| Torna al menu | `ESC`   |

#### Leaderboard

| Azione        | Tasto |
| ------------- | ----- |
| Torna al menu | `ESC` |

### Sistema di punteggio

| Linee completate | Punti base |
| ---------------- | ---------- |
| 1 linea          | 100        |
| 2 linee          | 300        |
| 3 linee          | 500        |
| 4 linee (Tetris) | 800        |

I punti vengono moltiplicati per il livello corrente. Il livello aumenta ogni 10 linee completate.

**Bonus drop:**

- **Soft drop**: 1 punto per ogni cella percorsa durante la caduta accelerata
- **Hard drop**: 2 punti per ogni cella di altezza da cui cade il pezzo

## Comandi di sviluppo

```bash
# Esecuzione del gioco
./gradlew run

# Verifica qualità codice (checkstyle, spotbugs, test)
./gradlew check

# Formatta il codice automaticamente
./gradlew spotlessApply

# Aggiorna le dipendenze
./gradlew --refresh-dependencies

# Compila la relazione PDF
./gradlew buildPdf

# Pulizia completa
./gradlew clean
```

## Download ed esecuzione

Requisito: [Java 21](https://adoptium.net/temurin/releases?version=21) o superiore.

### macOS / Linux

```bash
curl -L https://raw.githubusercontent.com/xpicio/tetraj/refs/heads/main/tetraj.jar -o /tmp/tetraj.jar && java -jar /tmp/tetraj.jar
```

### Windows (PowerShell)

```powershell
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/xpicio/tetraj/refs/heads/main/tetraj.jar" -OutFile "$env:TEMP\tetraj.jar"; java -jar "$env:TEMP\tetraj.jar"
```

## Ringraziamenti

Un ringraziamento a chi ha dedicato tempo a testare il gioco, segnalare bug e suggerire miglioramenti:

- **Pietro Benini**
- **Fabio Masini**
