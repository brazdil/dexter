.class public Luk/ac/cam/db538/dexter/tests/SourceTestExerciser;
.super Luk/ac/cam/db538/dexter/tests/TestExerciser;
.source "SourceTestExerciser.java"


# instance fields
.field private final test:Luk/ac/cam/db538/dexter/tests/SourceTest;


# direct methods
.method public constructor <init>(Luk/ac/cam/db538/dexter/tests/SourceTest;)V
    .registers 2
    .parameter "test"

    .prologue
    .line 10
    invoke-direct {p0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;-><init>()V

    .line 11
    iput-object p1, p0, Luk/ac/cam/db538/dexter/tests/SourceTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/SourceTest;

    .line 12
    return-void
.end method


# virtual methods
.method public getTest()Luk/ac/cam/db538/dexter/tests/Test;
    .registers 2

    .prologue
    .line 16
    iget-object v0, p0, Luk/ac/cam/db538/dexter/tests/SourceTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/SourceTest;

    return-object v0
.end method

.method public run()Z
    .registers 2

    .prologue
    .line 21
    iget-object v0, p0, Luk/ac/cam/db538/dexter/tests/SourceTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/SourceTest;

    invoke-interface {v0}, Luk/ac/cam/db538/dexter/tests/SourceTest;->generate()I

    move-result v0

    invoke-static {v0}, Luk/ac/cam/db538/dexter/tests/TaintChecker;->isTainted(I)Z

    move-result v0

    return v0
.end method
