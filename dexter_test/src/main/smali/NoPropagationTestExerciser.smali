.class public LNoPropagationTestExerciser;
.super LPropagationTestExerciser;

# direct methods
.method public constructor <init>(LPropagationTest;)V
    .registers 3

    # set shouldPropagate = false
    const/4 v0, 0
    invoke-direct {p0, p1, v0}, LPropagationTestExerciser;-><init>(LPropagationTest;Z)V

    return-void
    
.end method
