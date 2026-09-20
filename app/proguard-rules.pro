# Keep models for serialization
-keep class com.cyberexpert.androde.data.remote.dto.** { *; }
-keep class com.cyberexpert.androde.data.local.entity.** { *; }
-keep class com.cyberexpert.androde.domain.model.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation @kotlinx.serialization.Serializable class * {
    *;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <methods>;
}

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Androde IDE - Sora Editor
-keep class io.github.rosemoe.sora.** { *; }
-keep class io.github.rosemoe.sora_editor.** { *; }
-dontwarn io.github.rosemoe.sora.**

# Androde IDE - JGit
-keep class org.eclipse.jgit.** { *; }
-keep class org.eclipse.jgit.internal.** { *; }
-dontwarn org.eclipse.jgit.**
-dontwarn org.bouncycastle.**
-dontwarn com.jcraft.jsch.**
-keep class com.jcraft.jsch.** { *; }

# Androde IDE - LSP4J
-keep class org.eclipse.lsp4j.** { *; }
-dontwarn org.eclipse.lsp4j.**

# Commons IO
-keep class org.apache.commons.io.** { *; }
-dontwarn org.apache.commons.io.**

# Guava
-keep class com.google.common.** { *; }
-dontwarn com.google.common.**

# Androde IDE - Rhino JS Engine (Extension Host)
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.** { *; }
-dontwarn org.mozilla.javascript.**
-dontwarn org.mozilla.classfile.**

# Androde IDE - Snippets, Formatting, Emmet, Icon Themes, SSH, Remote, DAP, Workspace Trust
-keep class com.cyberexpert.androde.core.extensions.** { *; }
-keep class com.cyberexpert.androde.core.remote.** { *; }
-keep class com.cyberexpert.androde.core.workspace.** { *; }
-keep class com.cyberexpert.androde.data.local.extensions.** { *; }
-keep class com.cyberexpert.androde.data.local.snippets.** { *; }
-keep class com.cyberexpert.androde.data.local.formatting.** { *; }
-keep class com.cyberexpert.androde.data.local.emmet.** { *; }
-keep class com.cyberexpert.androde.data.local.icons.** { *; }
-keep class com.cyberexpert.androde.data.local.remote.** { *; }
-keep class com.cyberexpert.androde.data.local.workspace.** { *; }
-keep class com.cyberexpert.androde.data.local.diagnostics.** { *; }
-keep class com.jcraft.jsch.** { *; }
-dontwarn com.jcraft.jsch.**
