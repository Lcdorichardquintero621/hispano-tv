# HispanoTV - App de TV en Vivo y Películas

Una aplicación Android moderna para ver **TV en vivo (canales hispanohablantes gratis por país)** y **películas de estreno**, construida con arquitectura limpia, Jetpack Compose y Material 3.

## 🚀 GitHub Actions CI/CD

La app incluye configuración automática para GitHub Actions. Solo necesitas configurar los secrets:

### Configuración requerida

| Secret | Descripción | Dónde obtenerlo |
|--------|-------------|-----------------|
| `TMDB_API_KEY` | API Key de The MovieDB | [themoviedb.org/settings/api](https://www.themoviedb.org/settings/api) |
| `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` | Credenciales para Play Store | Google Play Console → Setup → Service Accounts |
| `SLACK_WEBHOOK` | Notificaciones (opcional) | Slack → Integraciones → Webhooks |
| `SONAR_TOKEN` | Análisis de código (opcional) | sonarcloud.io |

### Configurar secrets

```bash
# En GitHub: Settings → Secrets and variables → Actions → New repository secret
# Agrega cada secret con su valor correspondiente
```

### Workflow de GitHub Actions

El workflow está en: `.github/workflows/android.yml`

**Jobs automáticos:**
- ✅ **build**: Compila debug y release en Múltiples APIs (24-34)
- ✅ **analyze**: Análisis de código con SonarCloud
- ✅ **security**: Scanner de dependencias con OWASP Dependency Check
- ✅ **deploy**: Subida a Google Play (track: internal)
- ✅ **notify**: Notificaciones a Slack en caso de fallos

### Manual setup en GitHub

1. **Fork o clona el repositorio**
2. **Crea los secrets necesarios** (ver tabla arriba)
3. **Crea una Release** con tag v1.0.0 para generar build de release
4. **Los artifacts** se suben automáticamente como artifacts del workflow

### Ver logs de builds

```
Actions → Select workflow → android.yml → View build logs
Artifacts: download-debug-apk, download-release-apk, download-release-aab
```

---

## 🎯 Características

### TV en Vivo
- ✅ Canales organizados por **país** (20+ países hispanohablantes)
- ✅ Filtrado por **categoría** (Noticias, Deportes, Cine, Infantil, etc.)
- ✅ **Búsqueda en tiempo real** de canales
- ✅ Reproducción con **ExoPlayer** (HLS, DASH, RTMP)
- ✅ **Favoritos** persistentes
- ✅ UI optimizada para **Android TV / Fire TV**

### Películas
- ✅ **Estrenos en cines** (TMDB Now Playing)
- ✅ Películas **populares, mejor valoradas, próximamente**
- ✅ **Búsqueda** con autocompletado
- ✅ **Detalles completos** con metadatos y "dónde ver"
- ✅ **Watchlist** y favoritos

## 🏗️ Arquitectura

```
HispanoTV/
├── .github/workflows/android.yml    # CI/CD
├── app/                             # MainActivity, Navigation
├── core/
│   ├── common/                      # Theme, Components
│   ├── data/                        # Repositories, API, DB
│   └── domain/                      # Models, UseCases
├── feature/
│   ├── home/                        # MainScreen
│   ├── tv/                          # TV en vivo
│   └── movies/                      # Películas
└── README.md
```

## ⚙️ Configuración Rápida

### 1. Reemplazar API Key
Edita: `core/data/src/main/java/com/hispanotv/data/di/ConfigModule.kt`
```kotlin
return "TU_TMDB_API_KEY"
```

### 2. Fuentes M3U actualizadas
Edita: `core/data/src/main/java/com/hispanotv/data/di/DataModule.kt`

### 3. Compilar local
```bash
git clone <repo-url>
cd HispanoTV
./gradlew assembleDebug
./gradlew installDebug
```

## 📱 Stack Tecnológico

| Layer | Tecnología |
|-------|------------|
| UI | Jetpack Compose + Material 3 + Accompanist |
| Navegación | Navigation Compose |
| Arquitectura | Clean Architecture + MVVM |
| DI | Hilt |
| Data | Room, Retrofit, OkHttp, Moshi |
| Streaming | ExoPlayer (Media3) |
| Async | Coroutines + Flow + Paging 3 |

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Lint
./gradlew lintDebug
```

## 📦 Build Variants

| Variante | Descripción | Archivo |
|----------|-------------|---------|
| debug | Desarrollo | app-debug.apk |
| release | Producción | app-release.apk, app-release.aab |

## 🔒 Seguridad

- 🔐 API Keys en BuildConfig (no hardcodeados)
- 🛡️ ProGuard/R8 para release
- 📊 SBOM para auditoría de dependencias

## 🤝 Contribuir

1. Fork del repo
2. Crear branch: `feature/nueva-funcionalidad`
3. Commit: `feat: descripción`
4. Push y crear PR

## 📄 Licencia

MIT License

---

**Ready to deploy! 🚀**