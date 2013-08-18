.class public Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;
.super Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;

# direct methods
.method public constructor <init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    .registers 3

    # set shouldPropagate = false
    const/4 v0, 0
    invoke-direct {p0, p1, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;Z)V

    return-void
    
.end method
