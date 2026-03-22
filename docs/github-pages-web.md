# Publicar Material 2048 Web en GitHub Pages

## Requisitos previos
- JDK 17 instalado (la build usa Gradle + Compose Multiplatform).
- Git configurado con acceso al repositorio donde se publicará el sitio.
- (Opcional) Node.js/Caddy u otro servidor estático para probar la carpeta resultante.

## Construir y probar localmente
1. Limpia artefactos viejos y genera la distribución Wasm:
   ```bash
   ./gradlew :web:wasmJsBrowserDistribution
   ```
   En Windows PowerShell puedes usar `./gradlew.bat`.
2. Los archivos listos para publicar quedan en `web/build/dist/wasmJs/productionExecutable`.
3. Para previsualizar sin publicar, sirve esa carpeta con cualquier servidor estático, por ejemplo:
   ```bash
   cd web/build/dist/wasmJs/productionExecutable
   python -m http.server 8080
   ```

## Publicación manual (sin GitHub Actions)
1. Copia el contenido de `web/build/dist/wasmJs/productionExecutable` a una rama `gh-pages` (o a la carpeta `/docs` si prefieres Pages desde main).
2. Empuja los cambios y en **Settings → Pages** selecciona la rama/carpeta utilizada.
3. Espera a que GitHub procese el despliegue; la URL aparecerá ahí mismo.

## Publicación automática con GitHub Actions
Este repositorio ya incluye `.github/workflows/deploy-web.yml`.
1. Activa GitHub Pages para usar **GitHub Actions** como fuente (Settings → Pages → Build and deployment → Source: GitHub Actions).
2. Verifica que el workflow se ejecute en los pushes a `main`, `master` o `Web-Build` (puedes ajustar la lista de ramas en el yaml).
3. El flujo hace lo siguiente:
   - Configura JDK 17 y Gradle cache.
   - Ejecuta `./gradlew :web:wasmJsBrowserDistribution`.
   - Sube `web/build/dist/wasmJs/productionExecutable` como artefacto y lo publica vía `actions/deploy-pages`.
4. Si quieres publicar otra variante (por ejemplo la build de desarrollo), cambia la ruta del artefacto en el paso **Upload artifact**.

## Solución al error `./gradlew: Permission denied`
Ese error aparece cuando el script `gradlew` pierde el bit de ejecución al clonarse en Linux.

1. Desde cualquier clon local ejecuta:
   ```bash
   git update-index --chmod=+x gradlew
   git commit -m "fix: make gradlew executable"
   git push
   ```
   Esto fuerza a Git a recordar el permiso correcto y evita fallos futuros.
2. Aun así se mantiene el paso `chmod +x gradlew` dentro del workflow como red de seguridad.
3. Si el error persiste, confirma que el repositorio no esté montado en un sistema que ignora permisos (por ejemplo, un volumen NTFS) o añade `core.fileMode=true` en tu configuración de Git antes de subir cambios.

## Notas adicionales
- Si quieres usar un dominio personalizado, agrega un archivo `CNAME` dentro de `web/build/dist/wasmJs/productionExecutable` antes de subirlo (o genera el archivo en el workflow).
- Para builds más rápidas en CI puedes cachear `~/.gradle` (ya lo hace `gradle/actions/setup-gradle@v3`).
- Recuerda actualizar la meta `theme-color` o los assets en `web/src/wasmJsMain/resources` cuando cambies la paleta para que la pantalla de carga coincida con el juego.

