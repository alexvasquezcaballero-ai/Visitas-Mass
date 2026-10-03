# Visitas Mass

Aplicación Android nativa para visitas gerenciales a tiendas Mass. Registra visitas e indicadores, evalúa seis áreas operacionales y conserva localmente compromisos, fotografías adjuntas, historial y checklist configurable.

## Requisitos

- JDK 17 a 24 (JDK 17 recomendado; Gradle 8.14.3 no se ejecuta con JDK 25).
- Android SDK con Android 35 instalado y `ANDROID_HOME` o `ANDROID_SDK_ROOT` configurado.
- Gradle Wrapper incluido en el repositorio.

## Compilar APK debug

```bash
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

Los registros se guardan en el almacenamiento privado de la aplicación. Las fotografías se seleccionan desde el dispositivo y se conserva su URI. El memorándum se puede compartir como texto desde la pantalla correspondiente.
