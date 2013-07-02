; beenden mit s
HotKeySet("{ESC}", "stop")
$running = True;

; counter
$counter = 0

Sleep(2000)
   
While ($running)
   Sleep(500)
   MouseClick("left", 10, 30) ; Datei
   Sleep(100)
   MouseClick("left", 10, 50)	; Neues Sudoku
   Sleep(100)
   MouseClick("left", 10, 30)	; Datei
   Sleep(100)
   MouseClick("left", 10, 130)	; Speichern unter
   Sleep(100)
   Send("extreme" & $counter) ; Dateiname
   $counter = $counter + 1
   MouseClick("left", 800, 570) ; Format wählen
   Sleep(100)
   MouseClick("left", 800, 610) ; Text einzeilig
   Sleep (100)
   MouseClick("left", 900, 540) ; Speichern
WEnd

Func stop()
   $running = False
EndFunc