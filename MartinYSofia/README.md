# Proyecto Integrador – Organización y Arquitectura de Computadores

## Estudiantes

-   Nombre: Martín Quijano Hoyos
-   Nombre: Sofía Marín Bustamante

## Descripción

Este repositorio contiene la solución a los proyectos prácticos de la plataforma Nand2Tetris para el curso de Organización y Arquitectura de Computadores.

El proyecto abarca desde la construcción de circuitos lógicos, aritméticos y secuenciales (Proyectos 1, 2 y 3, incluyendo la extensión ALUExtendida), la programación en lenguaje ensamblador Hack (Proyecto 4), el diseño de la arquitectura completa del computador integrando memoria, CPU y periféricos (Proyecto 5), hasta el desarrollo del software de traducción mediante un Ensamblador y Desensamblador en Java (Proyecto 6).

---

# Contenido

## Proyecto 1

Implementación de las compuertas lógicas básicas y estructuras combinacionales derivadas.

### Componentes

-   Not
-   And
-   Or
-   Xor
-   Mux
-   DMux
-   Not16
-   And16
-   Or16
-   Mux16
-   Or8Way
-   Mux4Way16
-   Mux8Way16
-   DMux4Way
-   DMux8Way

---

## Proyecto 2

Implementación de circuitos aritméticos y de la ALU.

### Componentes

-   HalfAdder
-   FullAdder
-   Add16
-   Inc16
-   ALU

---

## Proyecto 3

Implementación de componentes secuenciales y memoria.

### Componentes

-   Bit
-   Register
-   RAM8
-   RAM64
-   RAM512
-   RAM4K
-   RAM16K
-   PC

---

## Extensión: ALUExtendida

La ALUExtendida conserva la interfaz de la ALU original de Nand2Tetris y agrega cinco operaciones nuevas.

### Operaciones soportadas

-   XOR
-   NAND
-   NOR
-   EQ
-   ABS

---

## Proyecto 4

Implementación de programas en lenguaje ensamblador Hack para fortalecer el control de flujo mediante saltos condicionales, la manipulación de registros y el direccionamiento de memoria directo e indirecto.

### Programas Oficiales

-   **Mult.asm**: Multiplica los valores almacenados en RAM[0] y RAM[1] mediante sumas sucesivas y almacena el resultado final en RAM[2].
-   **Fill.asm**: Escucha activamente la entrada del teclado (RAM[KBD]). Si se presiona cualquier tecla, escribe -1 en todo el mapa de memoria de la pantalla (SCREEN) para pintarla de negro; si no hay ninguna tecla presionada, escribe 0 para dejarla en blanco.

### Programas Adicionales del Curso

-   **SumN.asm**: Calcula la suma acumulada de los primeros N números naturales (1 + 2 + ... + N), donde la entrada es RAM[0] = N y el resultado se almacena en RAM[1].
-   **CopyBlock.asm**: Copia un bloque de datos de longitud N desde una dirección base de origen hacia una dirección destino en memoria, recibiendo las entradas en RAM[0] = ORIGEN, RAM[1] = DESTINO y RAM[2] = N.

---

## Proyecto 5

Implementación en HDL de la arquitectura de hardware completa del computador Hack, integrando unidades de almacenamiento, procesamiento y ejecución de instrucciones máquina.

### Componentes

-   **Memory**: Unidad de espacio de direcciones completa de la plataforma Hack (32K). Integra mediante multiplexación la memoria de datos (RAM16K), el mapa de bits de la pantalla (Screen) y el registro del teclado (Keyboard).
-   **CPU**: Unidad Central de Procesamiento encargada de decodificar y ejecutar instrucciones tipo A y tipo C, manipular los registros A, D y PC, operar con la ALU y controlar las condiciones de salto y escrituras en memoria.
-   **Computer**: Plataforma de hardware integrada de máximo nivel. Conecta la CPU, la memoria Memory y la memoria de instrucciones ROM32K para permitir la ejecución autónoma de programas.

---

## Proyecto 6

Implementación en Java de un software traductor bidireccional para la plataforma Hack: un ensamblador (de `.asm` a `.hack`) y un desensamblador (de `.hack` a `.asm`), diseñados con un manejo estricto de errores sintácticos y validación de línea.

### Módulos Principales

-   **Ensamblador**: Utiliza una arquitectura de dos pasadas para registrar etiquetas y asignar memoria a variables dinámicas. Está estructurado en las clases `HackAssembler`, `Parser`, `Code` y `SymbolTable`.
-   **Desensamblador**: Realiza ingeniería inversa sobre el binario reconstruyendo los mnemónicos simbólicos y detectando direcciones de salto mediante la clase `HackDisassembler` y `ReverseCode`.
-   **Scripts y Pruebas**: Archivos ejecutables Bash (`compilar.sh`, `ensamblador.sh` y `desensamblador.sh`) y un conjunto de pruebas unitarias diseñadas para validar tanto el éxito de la traducción como la correcta captura de errores (longitud, caracteres inválidos, etc.).

---

# Estructura del Repositorio

```         
proyecto01/ 
proyecto02/ 
proyecto03/ 
alu_extendida/ 
proyecto04/ 
├── HDL/ 
├── DOCUMENTACION/ 
└── TESTS/ 
proyecto05/ 
├── HDL/ 
├── DOCUMENTACION/ 
└── TESTS/
proyecto06/
├── ensamblador/
├── desensamblador/
├── pruebas/
├── Documentación/
├── README.md
├── compilar.sh
├── ensamblador.sh
└── desensamblador.sh
```
