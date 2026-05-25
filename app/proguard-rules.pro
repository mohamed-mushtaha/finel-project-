# Keep Firestore data classes (reflective field access)
-keep class com.hotel.hotelbooking.data.model.** { *; }

# Hilt
-keep class dagger.hilt.android.internal.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
