# Informe técnico — Proyecto 3: ensamblador y desensamblador Hack

Curso: Organización de computadores 2026-2 · Ingeniería de sistemas · Universidad EAFIT 

Docente: José Luis Montoya Pareja 


## 1. Integrantes del grupo y aportes

* **Sofía Marín Bustamante**
1. Implementación del ensamblador
2. Creación y revisión de pruebas ensamblador
3. Creación y revisión de archivos de compilación y formato de errores
4. Informe técnico y Readme general.


* **Martín Quijano Hoyos**
1. Implementación del desensambladr
2. Creación y revisión de pruebas desensamblador
3. Creación y revisión de casos de error, ensamblador y desensamblador
4. Readme de pruebas y errores.


## 2. Arquitectura de la solución

Dos programas Java independientes que comparten el estilo de diseño: cada uno separa la **lectura y validación del texto**, las **tablas de traducción** y el **programa principal**.

**Ensamblador** (`ensamblador/`)

| Módulo | Responsabilidad |
|---|---|
| `HackAssembler` | Coordina las dos pasadas, calcula la ruta de salida, controla los bloques `try-catch` para capturar excepciones, imprime errores con el formato exacto y borra archivos corruptos (en caso de que se detecte un error y el archivo de resultado fuese creado por algún motivo)|
| `Parser` | Limpia comentarios y espacios, cuenta líneas, clasifica la instrucción (A, C, etiqueta), la descompone y valida su estructura. |
| `Code` | Tablas `dest`, `comp` y `jump`; rechaza cualquier mnemónico que no esté en la especificación, lanzando excepciones |
| `SymbolTable` | Tabla de símbolos (predefinidos, etiquetas, variables), creadas con el nombre del simbolo y su dirección de memoria |

**Flujo de las dos pasadas**

1. **Pasada 1:** para cada instrucción, `Parser.validate()` revisa la estructura y redirecciona la validación más explicita a `Parser.validateA()`, `Parser.validateLabel()` o `Parser.validateC()` según sea el caso; `Code` revisa los mnemónicos (se traduce y se descarta el resultado si hay error), y las etiquetas se registran en la tabla con la dirección de la siguiente instrucción real (`romAddress`). Las etiquetas no ocupan dirección. Una etiqueta repetida o con nombre de símbolo predefinido es error y se reporta para abortar en `HackAssembler`
2. **Pasada 2:** se traduce cada instrucción a 16 bits. En `@símbolo`, si es número se usa directamente; si está en la tabla se usa su valor; si no, es una variable nueva y recibe la siguiente dirección de RAM libre desde la 16, que se ha estado contando con una variable de número de lineas.

**Decisión de diseño:** toda la validación ocurre en la pasada 1. Así el primer error reportado es el primero en orden de líneas (el enunciado lo exige) y el archivo `.hack` no se escribe en disco hasta que la entrada completa está 100% validada, garantizando que no se generen archivos binarios parciales corruptos.

**Desensamblador** (`desensamblador/`): `HackDisassembler` (lectura, validación de 16 bits, detección de saltos, reasignación de etiquetas y escritura) y ReverseCode (tablas de ingeniería inversa).

## 3. Tabla de símbolos

- **Estructura:** `HashMap<String, Integer>` (símbolo → dirección). Búsqueda e inserción en tiempo constante promedio, suficiente para programas de más de 27 000 instrucciones.

- **Contenido inicial:** `R0`–`R15` (0–15), `SP`, `LCL`, `ARG`, `THIS`, `THAT` (0–4), `SCREEN` (16384) y `KBD` (24576).

- **Etiquetas:** se agregan en la pasada 1. Antes de agregar una etiqueta se pregunta `contains`: como en ese momento la tabla solo tiene los predefinidos y las etiquetas anteriores, un `true` significa etiqueta repetida o nombre predefinido (error).

- **Variables:** se agregan en la pasada 2, con direcciones consecutivas desde la 16 en el orden de primera aparición (`ramAddress`). No pueden chocar con etiquetas porque estas ya están en la tabla.

## 4. Generación de etiquetas en el desensamblador

**Regla (sección 9.1).** Sea `n` el número de instrucciones. La instrucción A en la posición `i` se escribe con etiqueta si y solo si la instrucción `i+1` es una C con salto (bits `jjj` distintos de `000`) y el valor de la A es `v ≤ n`.

**Algoritmo (tres fases):**

1. Se leen y validan todas las líneas en una lista, atrapando errores de longitud o caracteres indebidos.
2. Para cada `i`, si `isJumpTarget(i)`, se agrega `v` a un `TreeSet<Integer>` (estructura elegida para guardar destinos sin repetidos y de forma estrictamente ordenada). Luego se recorre el conjunto en orden y se asigna `L0`, `L1`, … a cada dirección en un `HashMap<Integer, String>`. Así cada dirección destino tiene **una única etiqueta**, aunque varios saltos lleguen a ella.

3. Al emitir, antes de la instrucción `i` se escribe `(Lk)` si `i` es un destino; las A marcadas se escriben `@Lk`; las demás A quedan en decimal. Si el destino es `n`, la etiqueta se escribe al final del archivo.

**Ejemplo (`Prueba3.hack`, n = 16).** Los pares (4, 5), (8, 9) y (14, 15) son una A seguida de una C con salto, con valores 10, 12 y 14, todos ≤ 16. Los destinos son {10, 12, 14} y se nombran `L0`, `L1`, `L2`. El resultado coincide con el ejemplo del enunciado (sección 9.2).

## 5. Manejo de errores

| Categoría | Cómo se detecta | Dónde |
|---|---|---|
| Instrucción A | Valor vacío; si empieza con dígito debe ser solo dígitos y ≤ 32767 (se compara con `BigInteger` para evitar desbordes); si no, debe cumplir `[A-Za-z_.$:][A-Za-z0-9_.$:]*` | `Parser.validateA` |
| Etiquetas | Falta `)`, nombre vacío o inválido; nombre ya presente en la tabla (repetida o predefinida) | `Parser.validateLabel`, `HackAssembler` |
| Destino, cómputo, salto | Se distingue `null` (campo ausente) de `""` (campo presente pero vacío); el texto debe estar exactamente en la tabla (sensible a mayúsculas) | `Code` |
| Estructura | Más de un `=`, más de un `;`, `;` antes de `=` | `Parser.validateC` |
| Desensamblador | Longitud distinta de 16, caracteres distintos de `0`/`1`, bits 14–13 distintos de `11`, código de cómputo inexistente | `HackDisassembler.validateLine` |

**Número de línea.** `Parser` (y el escáner del desensamblador) incrementa la variable que cuenta la línea actual(`currentLine`) en **cada** iteración de lectura (`nextLine()`), antes de limpiar nada, de modo que comentarios y líneas en blanco se cuentan. El desensamblador cuenta igual y **no** usa `trim()` en su pasada de validación, ni salta líneas vacías, porque un espacio al final o una línea vacía son errores de longitud. Los errores se señalan con `IllegalArgumentException` o capturando las nativas de Java; un único punto en cada clase principal (`reportError`) imprime `ERROR *** Línea N: <mensaje>` configurado estrictamente en UTF-8 para garantizar la visualización de la tilde en la 'í' sin importar la consola (macOS o Git Bash en Windows), borra el archivo de salida usando new File().delete() y termina la ejecución con System.exit(1), para segurar un código diferente de 0 (que es cuando para cuando sea exitoso)

## 6. Pruebas realizadas y resultados

El detalle de cada caso está en `pruebas/README.md`

| Grupo | Casos | Resultado |
|---|---|---|
| Ensamblador, programas válidos | 3 | 100% exitoso (archivos .hack generados idénticos al oficial y en la misma carpeta |
| Desensamblador, programas válidos (+ condición de corrección) | 3 | 100% exitoso (ciclo ensamblar(ProgDis.asm) = Prog.hack comprobado) y también se genera en la misma carpeta |
| Errores del ensamblador (10.1) | 35 | 100% exitoso (programa aborta, no deja archivos corruptos, formato exacto impreso y la línea correcta) |
| Errores del desensamblador (10.2) | 10 | 100% exitoso (errores de longitud, cómputos inválidos atrapados) |

Otras verificaciones superadas: los archivos binarios compilados por nuestra herramienta se ejecutaron correctamente en el CPU Emulator de la suite oficial Nand2Tetris y en consola al descargar esos mismos archivos, los del proyecto6

## 7. Análisis (RA4)

**¿Qué información se pierde al ensamblar?** Los comentarios, el formato (espacios, tabulaciones, sangría, líneas en blanco), los nombres de las etiquetas y de las variables y el uso de símbolos predefinidos (`@R0`, `@SCREEN`), que son reemplazados directamente por la dirección de memoria. Las declaraciones `(ETIQUETA)` ni siquiera ocupan una linea de instrucción (se traducen como la dirección de memoria de la instrucción de la linea siguiente a esa etiqueta)

**¿Por qué el desensamblado no es único?**  Porque programas fuente distintos producen el mismo binario exacto. Por ejemplo, `@sum` (si es la primera variable), `@R16` y `@16` generan la misma instrucción de 16 bits, si sum estaba apuntando a la dirección de memoria 16; los nombres lógicos de las etiquetas son arbitrarios y el espaciado o los comentarios se destruyen, por lo que una línea traducida a binario pudo haber provenido de una variable con infinidad de nombres diferentes. Por eso la condición de corrección se define sobre el binario (ensamblar(ProgDis.asm) = Prog.hack) y no directamente sobre el texto.

**¿Por qué el binario no distingue una variable de una constante o de un símbolo predefinido?** Porque a nivel de hardware, en la arquitectura Hack que estás construyendo, la instrucción tipo A (`@`) tiene una única función: cargar un número de 15 bits en el procesador. Las instrucciones `@16`, `@R16` y una variable asignada dinámicamente a la dirección 16 resultan en el mismo patrón `0000000000010000`, no se distinguen por su nombre anterior: el número final es lo único que el procesador necesita y conserva, perdiéndose el origen o el significado que le había dado el programador, por eso no se diferencia si es constante o símbolo, solo se distingue, a nivel de máquina, que es el mismo binario. 

**¿Qué saltos no puede etiquetar la regla de 9.1 y por qué?**
- Saltos cuya instrucción anterior **no es una instrucción tipo A**: la dirección se calculó o se cargó antes (`A=D` seguido de `0;JMP`) o hay instrucciones entre la A y el salto (`@FIN`, `D=M`, `D;JGT`: el salto necesita usar la A del momento para poder almacenar la dirección de memoria de la instrucción a la que redirigía el salto, pero se encuentra con que la instrucción previa es una C, de donde no puede obtener dicha información).
* Saltos cuya A tiene un valor **mayor que n**: el destino queda por fuera del alcance lógico del programa y no existe una instrucción física delante de la cual anclar y declarar la etiqueta.
