# README

## Integrantes del grupo
* Sofía Marín Bustamante
* Martín Quijano Hoyos

## Compilación y ejecución

**Lenguaje y versión:** Java (JDK 27)
Instalado por medio del navegador web, en la página de Oracle: 'Java SE Development Kit 27.0.1.0.3 downloads'. Se seleccionó la opción de 'macOS' y el según archivo 'ARM64 DMG Installer' (<https://download.oracle.com/java/27/latest/jdk-27_macos-aarch64_bin.dmg>)
Para correrlo es necesario también tener Git para Windows, de modo que se pueda emplear 

*Resultado:*
```bash
    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % java --version
    java 27.0.1.0.3 2026-10-25
    Java(TM) SE Runtime Environment (build 27.0.1.0.3+1-2)
    Java HotSpot(TM) 64-Bit Server VM (build 27.0.1.0.3+1-2, mixed mode, sharing)
```

**Dependencias:** Este proyecto ha sido desarrollado utilizando únicamente las librerías estándar de Java. No requiere la instalación de dependencias externas. Solo es necesario contar con un JDK correctamente configurado en el `PATH` del sistema, por ende la instalación sería la vista anteriormente (en este caso se emplearon todas las rutas predeterminadas sugeridas por el instalador de Java).

### Comandos de ejecución:

Para asegurar la correcta ejecución del sistema y la validación de errores, el proyecto se debe operar mediante los scripts de Bash que se encuentran en la raíz del proyecto: `compilar.sh`, `ensamblador.sh` y `desensamblador.sh`. Los comandos exactos son los siguientes:

*Todos los comandos se ejecutan desde la carpeta `proyecto3/`*

1.  **Dar permisos de ejecución (Para macOS/Linux):**
    ```bash
    chmod +x *.sh
    ```
2.  **Compilar el proyecto:**
    Este comando compila todos los archivos `.java` de las carpetas `ensamblador` y `desensamblador`. Si es exitoso, finalizará sin hacer nada visible en la pantalla.
    ```bash
    ./compilar.sh
    ```
3.  **Ejecutar el ensamblador:**
    Convierte código `.asm` a `.hack` (binario). El código `.hack` se genera en la misma ruta del archivo `.asm` procesado y con el mismo nombre, solo cambia su extensión.
    ```bash
    ./ensamblador.sh ruta/al/archivo.asm
    ```
4.  **Ejecutar el desensamblador:**
    Convierte código binario a `.asm`. El código `.asm` se genera en la misma ruta del archivo `.hack` procesado y con el mismo nombre + 'Dis' al final, quedaría con la forma: `nombreOriginalDis.hack`
    ```bash
    ./desensamblador.sh ruta/al/archivo.hack
    ```
**Notas:**
* La ruta puede ser relativa o absoluta y **puede contener espacios** (los scripts la manejan entre comillas). Ejemplo: `./ensamblador.sh "C:/Users/Ana/OneDrive/Mis proyectos/Prog.asm"`
* Si Git Bash responde `Permission denied` al ejecutar un script, usar bash `compilar.sh` o dar permiso con `chmod +x *.sh`.
* Si ocurre un error en la entrada, el programa imprime una sola línea ERROR *** Línea N: <mensaje>, no genera el archivo de salida (y borra uno anterior si existía) y termina con código de salida distinto de 0. Si todo va bien, el código de salida es 0.

## Descripción breve de la estructura del código

El proyecto está diseñado de forma modular y se divide en tres carpetas principales:

*   **`ensamblador/`**: Contiene la lógica para traducir de `.asm` a `.hack`. Implementa una lógica de dos pasadas, en la primera se realizan todas las validaciones para implementar la lógica de los errores indicada por el profesor.
    *   `HackAssembler.java`: Clase principal que coordina los ciclos de lectura, controla los bloques `try-catch` para la captura y el reporte de errores, y aborta la escritura de archivos corruptos.
    *   `Parser.java`: Descompone cada instrucción en sus partes lógicas y valida su sintaxis estructural.
    *   `Code.java`: Traduce los mnemónicos (particulas) simbólicos (comp, dest, jump) a sus equivalentes binarios y detecta combinaciones no válidas.
    *   `SymbolTable.java`: Gestiona la tabla hash que almacena las variables y las etiquetas lógicas con sus respectivas direcciones de memoria.
*   **`desensamblador/`**: Contiene la lógica para reconstruir un archivo `.asm` desde un `.hack`, binario.
    *   `HackDisassembler.java`: Lee las cadenas de 16 bits, detecta los saltos, reasigna etiquetas y captura excepciones para cumplir con el formato de error indicado.
    *   `ReverseCode.java`: Realiza el trabajo inverso a la clase `Code` del ensamblador, mapeando los bloques binarios de regreso a los comandos `asm` correspondientes para ir armando la instrucción completa en la clase `HackDisassembler.java`.
*   **`pruebas/`**: Contiene códigos que son válidos y generados por nosotros para probar la funcionalidad de ensamblador y el desensamblador, y también   archivos con un tipo de error definido para asegurar la detección de errores y su formato.
    *   `ensamblador/`: Contiene 3 archivos `.asm` sin errores, diferentes a los oficiales de Nand2Tetris y a los otorgados por el profesor, para revisar que sí se genere la correcta tradución a `.hack`. Los archivos son: `MultiplicarPorCinco.asm`, `PatronPunteado.asm` y `SumarArreglo.asm`.
    *   `desensamblador/`: Contiene 3 archivos `.hack` sin errores, diferentes a los oficiales de Nand2Tetris y a los otorgados por el profesor, para revisar que sí se genere la correcta tradución a `.asm`. Los archivos son: `BucleSuma.hack`, `Intercambio.hack` y `ValorAbsoluto.hack`.
    *   `errores/`: carpeta con un archivos con diferentes tipos de errores, según las categorías definidas en el enunciado del proyecto, para evaluar que sí se detecten los errores indicados correctamente y se respete el formato solicitado. No se separan los archivos de error para el ensamblador y el desensamblador, pero se reconocen por su extensión y las categorias de error fe su nombre.
    Para el ensamblador: `ErrorComputo.asm`, `ErrorDestino.asm`, `ErrorEtiqueta.asm`, `ErrorInstruccionA.asm`, `ErrorSalto.asm` y `ErrorEstructura.asm`
    Para el desensamblador: `ErrorLongitud.hack`, `ErrorComputoDis.hack`, `ErrorCaracteres.hack` y `ErrorBits.hack`

## Estructura del repositorio

```bash
    proyecto3/
    ├── compilar.sh              compila ensamblador y desensamblador
    ├── ensamblador.sh           ./ensamblador.sh
    ├── desensamblador.sh        ./desensamblador.sh
    ├── README.md                este archivo
    ├── .gitignore               excluye bin/, *.class, carpetas de vscode 
    ├── ensamblador/             código fuente del ensamblador
    │   ├── HackAssembler.java
    │   ├── Parser.java
    │   ├── Code.java
    │   └── SymbolTable.java
    ├── desensamblador/          código fuente del desensamblador
    │   ├── HackDisassembler.java
    │   └── ReverseCode.java
    ├── pruebas/
    │   ├── ensamblador/         3 programas válidos propios (.asm)
    │   ├── desensamblador/      3 programas válidos propios (.hack)
    │   ├── errores/             un caso por cada categoría de error (.asm y .hack)
    │   └── README.md            descripción y resultado esperado de cada caso
    └── DOCUMENTACION/
        └── INFORME.md           informe técnico
```