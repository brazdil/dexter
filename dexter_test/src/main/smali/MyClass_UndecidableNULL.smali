.class public LMyClass_UndecidableNULL;
.super Ljava/lang/Object;

.field private static X:Ljava/io/File;
.field private static Y:LMyClass_Point;

# direct methods
.method public constructor <init>()V
    .registers 5

    const/4 v0, 0x0

    sput v0, LMyClass_UndecidableNULL;->X:Ljava/io/File;
    sput v0, LMyClass_UndecidableNULL;->Y:LMyClass_Point;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method
