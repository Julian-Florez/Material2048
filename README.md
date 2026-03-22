# Material 2048 🔢

Una versión moderna del clásico **2048**, diseñada con **Material Design 3** y construida con **Kotlin Multiplatform** y **Compose**.

## 🎮 Jugar Online

[**¡Juega ahora en tu navegador!**](https://julian-florez.github.io/Material2048/)

## 🚀 Plataformas

* **Web (WebAssembly)** – Juega directamente en el navegador
* **Desktop** – Windows, macOS, Linux
* **Android**

## 🎯 Controles

* **Flechas del teclado** o **WASD** – Mover las fichas
* **R** – Reiniciar la partida
* **Pantalla táctil** – Desliza en cualquier dirección (en móviles)

## 🛠️ Tecnologías

* Kotlin Multiplatform
* Jetpack Compose / Compose Multiplatform
* Material Design 3
* WebAssembly (Kotlin/Wasm)

## 📦 Compilar

### Web

```bash
./gradlew :web:wasmJsBrowserDevelopmentRun
```

### Desktop

```bash
./gradlew :desktop:run
```

### Android

Ejecutar en un dispositivo o emulador con la build de debug:

```bash
./gradlew :app:installDebug
```

Generar el APK listo para distribuir (carpeta `app/build/outputs/apk/release/`):

```bash
./gradlew :app:assembleRelease
```

### Instalador Windows (MSI)

Requiere WiX 3.11 (el repo ya incluye una copia portátil en `build/wix311`). El instalador se genera en `desktop/build/compose/binaries/main/msi/`:

```bash
./gradlew :desktop:packageReleaseMsi
```

### Producción Web

```bash
./gradlew :web:wasmJsBrowserDistribution
```