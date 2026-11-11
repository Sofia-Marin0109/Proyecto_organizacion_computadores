**Organización de computadores**
**Proyecto 4 - Programación**
**2026-2**

# Integrantes

| Nombre | Usuario GitHub | Aportes principales |
|---|---|---|
| Sofía Marín Bustamante | Sofia-Marin0109 |Encargada de crear la clase Juego, Carrera, Main y Obstaculo; y  subir todo al repositorio |
| Martín Quijano Hoyos| mquijanoh09|Encargado de crear Aleatorio, Gráfico, Vista y el README  |

# Compilación y ejecución

1. Compilar la carpeta con el compilador de las herramientas del curso:
   - Windows: `tools/JackCompiler.bat RUTA/proyecto4`
   - macOS / Linux: `tools/JackCompiler.sh RUTA/proyecto4`
2. Abrir el VM Emulator (`tools/VMEmulator.bat` o `tools/VMEmulator.sh`).
3. «File → Load Program» y elegir la carpeta `proyecto07` (sin agregar los `.vm` del sistema operativo: el emulador usa los suyos).
4. Seleccionar «No animation», poner la velocidad al máximo y presionar «Run».
5. Hacer clic en la pantalla del emulador para que reciba las teclas.

Las pruebas públicas se corren con:

```
cd material-apoyo/pruebas
./correr_pruebas.sh RUTA/proyecto07 RUTA/nand2tetris/tools
```

# Controles

| Tecla | Acción |
|---|---|
| 1, 2, 3 | En la pantalla inicial: elegir la meta (100, 200, 300 pasos) |
| Flecha arriba (131) | Subir un carril (una vez por pulsación) |
| Flecha abajo (133) | Bajar un carril (una vez por pulsación) |
| P (80) | Pausar y continuar |
| R (82) | Volver a la pantalla inicial sin recargar el programa |
| ESC (140) | Salir del juego |

# Diseño de clases

| Clase | Responsabilidad |
|---|---|
| `Main` | Solo crea el `Juego`, lo ejecuta y lo libera. |
| `Juego` | Control del juego: menú, bucle de la carrera, lectura del teclado con `Keyboard.keyPressed()`, pausa, reinicio y salida. |
| `Vista` | Presentación: crea los sprites y dibuja la vía, el carro, los obstáculos, el marcador y los mensajes. Lee el estado de la carrera pero nunca lo cambia. |
| `Carrera` | Lógica del juego (sección 8): carril del carro, vidas, puntaje, progreso, estado, obstáculos, choques y generación. No usa `Screen`, `Output`, `Keyboard`, `Grafico` ni `Sys.wait`. |
| `Obstaculo` | Un obstáculo cualquiera: posición, columna anterior, largo, velocidad y si está activo. |

**Especialización sin herencia.** Hay una sola clase `Obstaculo` para los 4 tipos y se especializa por datos: el constructor recibe el tipo y con él fija el largo y la velocidad (el camión mide 3 celdas y la moto avanza 2 por paso). Los métodos `mover`, `ocupa`, `pasoPor` y `salio` son los mismos para todos porque solo usan esos datos. En la vista pasa lo mismo: `sprites[tipo]` es el dibujo de cada tipo, entonces no hay un `if` por tipo para dibujar.

**Lógica separada de la presentación.** `Carrera` y `Obstaculo` solo guardan y actualizan datos; por eso las pruebas automáticas pueden usar `Carrera` sin pantalla ni teclado. `Juego` decide cuándo avanzar y `Vista` dibuja lo que hay en la carrera en cada paso.

**Memoria.** Cada obstáculo eliminado se libera con `dispose` en `Carrera.eliminarInactivos`, `Carrera.dispose` libera los que quedan y el arreglo, y cada partida libera su carrera al terminar. Los sprites se crean una sola vez en `Vista` y los textos se liberan apenas se escriben (`Vista.escribir`).

# Rutinas VM

| Función | Archivo | Qué hace | Dónde la usa el juego |
|---|---|---|---|
| `Grafico.direccion(fila, col)` | `Grafico.vm` | Calcula `16384 + fila*512 + col` sumando 512 una vez por fila (sin `Math.multiply`). | La llaman las otras funciones de `Grafico`. |
| `Grafico.dibujarCelda(fila, col, sprite)` | `Grafico.vm` | Copia las 16 palabras del sprite a la celda usando `pointer 1` y `that`. | `Vista.dibujarVia`, `Vista.pintarEntidades` y `Vista.pintarObstaculo` (carro, obstáculos, bordes y líneas). |
| `Grafico.borrarCelda(fila, col)` (bono) | `Grafico.vm` | Pone en 0 las 16 palabras de la celda. | `Vista.pintarEntidades` y `Vista.pintarObstaculo`, para borrar antes de cada paso. |
| `Grafico.desplazarFila(fila)` (bono) | `Grafico.vm` | Rota una palabra a la izquierda las 16 líneas de la fila, con la ayuda de `Grafico.rotarLinea`. | `Vista.animarLineas`, para mover las líneas divisorias (filas 6 y 8). |
| `Aleatorio.semilla(s)` y `Aleatorio.siguiente()` (bono) | `Aleatorio.vm` | Generador `x = 5x + 3` de 16 bits calculado con sumas. | `Carrera.new` y `Carrera.azar` (generación de obstáculos). |

# Uso de IA

Se empleó Gemini para comprender cómo se gestiona la compilación, las pruebas y el VM Emulator: cómo descargar el software de Nand2Tetris, cómo compilar los archivos que creamos y probarlos con los recursos otorgados por el profesor, etc. Principalmente para los comandos y la gestión de la consola.

Se utilizó Claude para aclarar dudas conceptuales sobre los temas de la entrega y, pricipalmente, para la solución de errores como los dibujos en pantalla (no lograbamos que aparecieran motos o buses, solo generaba conos y de manera exagerda), el overheap y demás temas. 

Utilizamos los recursos de las clases y de Nand2Tetris para intentar comprender más a fondo antes de consultar a la IA como implementar alguna funcionaldad que no nos funcionaba o desconociamos.  
