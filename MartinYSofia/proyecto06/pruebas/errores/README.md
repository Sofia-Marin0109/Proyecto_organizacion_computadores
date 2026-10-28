# Pruebas del Proyecto 3 - Organización de Computadores

## 1. Programas Válidos (Ensamblador)
* **CopyBlock.asm:** Genera `CopyBlock.hack` correspondiente al `.asm`. Es un algoritmo que copia datos entre una memoria de origen y destino usando punteros, (trabajado en clase)
* **DibujarPatron.asm:** Genera `DibujarPatron.hack` correspondiente al `.asm`. Dibuja un patrón punteado vertical en el lado izquierdo de la pantalla iterando 256 veces (trabajado en clase)
* **LlenarMemoria.asm:** Genera `LlenarMemoria.hack` correspondiente al `.asm`. Escribe el valor 7 en un bloque consecutivo de memoria, usando R0 como puntero base y R1 como longitud, (trabajado en clase, pero con -1).

## 2. Programas Válidos (Desensamblador)
* **BucleSuma.hack:** Genera `BucleSumaDis.asm`. Prueba la reconstrucción de un bucle `while` con una etiqueta de retorno y una etiqueta de salida. Al ensamblarlo nuevamente, el binario es idéntico al original.
* **Intercambio.hack:** Genera `IntercambioDis.asm`. Prueba un bloque de manipulación de memoria lineal usando una variable temporal, finalizando con un salto infinito.
* **ValorAbsoluto.hack:** Genera `ValorAbsolutoDis.asm`. Prueba la reconstrucción de un salto condicional hacia adelante (como una bifurcación `if-else`).

## 3. Casos de Error Esperados (Ensamblador)
* **ErrorInstruccionA.asm:**  ERROR *** Línea 5: valor fuera de rango '32768' (máximo 32767)
* **ErrorEtiqueta.asm:** ERROR *** Línea 15: nombre de etiqueta inválido '%STOP'
* **ErrorDestino.asm:** ERROR *** Línea 21: destino inválido 'DM'
* **ErrorComputo.asm:** ERROR *** Línea 23: Cómputo inválido 'M+D'
* **ErrorSalto.asm:** ERROR *** Línea 18: salto inválido 'jmp'
* **ErrorEstructura.asm:** ERROR *** Línea 6: la instrucción tiene más de un '='

*Muestra de la salida en consola al correrlo en una de las pruebas:*

```bash
    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./ensamblador.sh pruebas/errores/ErrorComputo.asm 
    ERROR *** Línea 23: Comp inválido 'M+D'

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./ensamblador.sh pruebas/errores/ErrorDestino.asm
    ERROR *** Línea 21: destino inválido 'DM'

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./ensamblador.sh pruebas/errores/ErrorEstructura.asm
    ERROR *** Línea 6: la instrucción tiene más de un '='

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./ensamblador.sh pruebas/errores/ErrorEtiqueta.asm
    ERROR *** Línea 15: nombre de etiqueta inválido '%STOP'

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./ensamblador.sh pruebas/errores/ErrorInstruccionA.asm
    ERROR *** Línea 5: valor fuera de rango '32768' (máximo 32767)

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./ensamblador.sh pruebas/errores/ErrorSalto.asm
    ERROR *** Línea 18: salto inválido 'jmp'
```

## 4. Casos de Error Esperados (Desensamblador)
* **ErrorLongitud.hack:** ERROR *** Línea 7: longitud inválida: tiene 15 caracteres y deben ser 16
* **ErrorCaracteres.hack:** ERROR *** Línea 6: carácter inválido 'A' en la posición 9
* **ErrorBits.hack:** ERROR *** Línea 5: los bits 14 y 13 de una instrucción C deben ser 11
* **ErrorComputoDis.hack:** ERROR *** Línea 6: código de cómputo inválido '0000001'

*Muestra de la salida en consola al correrlo en una de las pruebas:*

```bash
    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./desensamblador.sh pruebas/errores/ErrorBits.hack
    ERROR *** Línea 5: los bits 14 y 13 de una instrucción C deben ser 11

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./desensamblador.sh pruebas/errores/ErrorCaracteres.hack
    ERROR *** Línea 6: carácter inválido 'A' en la posición 9

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./desensamblador.sh pruebas/errores/ErrorLongitud.hack  
    ERROR *** Línea 7: longitud inválida: tiene 15 caracteres y deben ser 16

    sofi_marin@Sofias-MacBook-Air-3 proyecto3 % ./desensamblador.sh pruebas/errores/ErrorComputoDis.hack
    ERROR *** Línea 6: código de cómputo inválido '0000001'
```

