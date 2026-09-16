# R8 / Proguard Rules for ExpenseLite

# Kotlinx Serialization Keep Rules
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class *$$serializer {
    *** INSTANCE;
}
-keepclassmembers class * {
    *** companion(...);
}

# Keep Navigation Compose AppRoute implementations
-keep class com.amehran.expenselite.presentation.navigation.AppRoute** { *; }

# Keep Room Database Entities and DAOs
-keep class com.amehran.expenselite.data.local.entity.** { *; }
-keep class com.amehran.expenselite.data.local.dao.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}

# Keep Domain Models used in Serialization / Export
-keep class com.amehran.expenselite.domain.model.** { *; }
