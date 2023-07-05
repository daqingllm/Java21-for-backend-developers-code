package chapter1;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.*;

public final class VCompletableFuture<T> implements Future<T>, CompletionStage<T> {

    private static final ExecutorService vcfExecutor = Executors.newVirtualThreadPerTaskExecutor();

    private CompletableFuture<T> completableFuture;

    private VCompletableFuture() {}

    public static <U> VCompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = CompletableFuture.supplyAsync(supplier, vcfExecutor);
        return vcf;
    }

    public static VCompletableFuture<Void> runAsync(Runnable runnable) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = CompletableFuture.runAsync(runnable, vcfExecutor);
        return vcf;
    }

    public static <U> VCompletableFuture<U> completedFuture(U value) {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = CompletableFuture.completedFuture(value);
        return vcf;
    }

    public static VCompletableFuture<Void> allOf(VCompletableFuture<?>... vcfs) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        CompletableFuture<?>[] cfs = Arrays.stream(vcfs).map(vf -> vf.completableFuture).toList().toArray(new CompletableFuture<?>[0]);
        vcf.completableFuture = CompletableFuture.allOf(cfs);
        return vcf;
    }

    public static VCompletableFuture<Object> anyOf(VCompletableFuture<?>... vcfs) {
        VCompletableFuture<Object> vcf = new VCompletableFuture<>();
        CompletableFuture<?>[] cfs = Arrays.stream(vcfs).map(item -> item.completableFuture).toList().toArray(new CompletableFuture<?>[0]);
        vcf.completableFuture = CompletableFuture.anyOf(cfs);
        return vcf;
    }

    public boolean isDone() {
        return completableFuture.isDone();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    public boolean isCompletedExceptionally() {
        return completableFuture.isCompletedExceptionally();
    }

    public void obtrudeValue(T value) {
        completableFuture.obtrudeValue(value);
    }

    public void obtrudeException(Throwable ex) {
        completableFuture.obtrudeException(ex);
    }

    public int getNumberOfDependents() {
        return completableFuture.getNumberOfDependents();
    }

    public <U> VCompletableFuture<U> newIncompleteFuture() {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = new CompletableFuture<>();
        return vcf;
    }

    public Executor defaultExecutor() {
        return vcfExecutor;
    }

    public VCompletableFuture<T> copy() {
        VCompletableFuture<T> vcf = new VCompletableFuture<>();
        vcf.completableFuture = this.completableFuture.copy();
        return vcf;
    }

    @SuppressWarnings("unchecked")
    public T get() throws InterruptedException, ExecutionException {
        return completableFuture.get();
    }

    @SuppressWarnings("unchecked")
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return completableFuture.get(timeout, unit);
    }

    @SuppressWarnings("unchecked")
    public T join() {
        return completableFuture.join();
    }

    @SuppressWarnings("unchecked")
    public T getNow(T valueIfAbsent) {
        return completableFuture.getNow(valueIfAbsent);
    }

    @Override
    public T resultNow() {
        return completableFuture.resultNow();
    }

    @Override
    public Throwable exceptionNow() {
        return completableFuture.exceptionNow();
    }

    @Override
    public State state() {
        return completableFuture.state();
    }

    public VCompletableFuture<T> completeAsync(Supplier<? extends T> supplier) {
        this.completableFuture = this.completableFuture.completeAsync(supplier, vcfExecutor);
        return this;
    }

    public VCompletableFuture<T> orTimeout(long timeout, TimeUnit unit) {
        this.completableFuture = this.completableFuture.orTimeout(timeout, unit);
        return this;
    }

    public VCompletableFuture<T> completeOnTimeout(T value, long timeout,
                                                   TimeUnit unit) {
        this.completableFuture = this.completableFuture.completeOnTimeout(value, timeout, unit);
        return this;
    }

    public boolean complete(T value) {
        return completableFuture.complete(value);
    }

    public boolean completeExceptionally(Throwable ex) {
        return completableFuture.completeExceptionally(ex);
    }

    public <U> VCompletableFuture<U> thenApply(Function<? super T,? extends U> fn) {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.thenApply(fn);
        return vcf;
    }

    public <U> VCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return thenApply(fn);
    }

    @Deprecated
    public <U> VCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return thenApply(fn);
    }

    public VCompletableFuture<Void> thenAccept(Consumer<? super T> action) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.thenAccept(action);
        return vcf;
    }

    public VCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
        return thenAccept(action);
    }

    @Deprecated
    public VCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return thenAccept(action);
    }

    public VCompletableFuture<Void> thenRun(Runnable action) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.thenRun(action);
        return vcf;
    }

    public VCompletableFuture<Void> thenRunAsync(Runnable action) {
        return thenRun(action);
    }

    @Deprecated
    public VCompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
        return thenRun(action);
    }

    public <U, V> VCompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        VCompletableFuture<V> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.thenCombine(other, fn);
        return vcf;
    }

    public <U, V> VCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombine(other, fn);
    }

    @Deprecated
    public <U, V> VCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return thenCombine(other, fn);
    }

    public <U> VCompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.thenAcceptBoth(other, action);
        return vcf;
    }

    public <U> VCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return thenAcceptBoth(other, action);
    }

    @Deprecated
    public <U> VCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return thenAcceptBoth(other, action);
    }

    public VCompletableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.runAfterBoth(other, action);
        return vcf;
    }

    public VCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return runAfterBoth(other, action);
    }

    @Deprecated
    public VCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return runAfterBoth(other, action);
    }

    public <U> VCompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.applyToEither(other, fn);
        return vcf;
    }

    public <U> VCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEither(other, fn);
    }

    @Deprecated
    public <U> VCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
        return applyToEither(other, fn);
    }

    public VCompletableFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.acceptEither(other, action);
        return vcf;
    }

    public VCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEither(other, action);
    }

    @Deprecated
    public VCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return acceptEither(other, action);
    }

    public VCompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        VCompletableFuture<Void> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.runAfterEither(other, action);
        return vcf;
    }

    public VCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return runAfterEither(other, action);
    }

    @Deprecated
    public VCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return runAfterEither(other, action);
    }

    public <U> VCompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.thenCompose(fn);
        return vcf;
    }

    public <U> VCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return thenCompose(fn);
    }

    @Deprecated
    public <U> VCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return thenCompose(fn);
    }

    public <U> VCompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        VCompletableFuture<U> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.handle(fn);
        return vcf;
    }

    public <U> VCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return handle(fn);
    }

    @Deprecated
    public <U> VCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return handle(fn);
    }

    public VCompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        VCompletableFuture<T> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.whenComplete(action);
        return vcf;
    }

    public VCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return whenComplete(action);
    }

    @Deprecated
    public VCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return whenComplete(action);
    }

    public VCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
        VCompletableFuture<T> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.exceptionally(fn);
        return vcf;
    }

    public VCompletableFuture<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) {
        return exceptionally(fn);
    }

    @Deprecated
    public VCompletableFuture<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) {
        return exceptionally(fn);
    }

    public VCompletableFuture<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) {
        VCompletableFuture<T> vcf = new VCompletableFuture<>();
        vcf.completableFuture = completableFuture.exceptionallyCompose(fn);
        return vcf;
    }

    public VCompletableFuture<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyCompose(fn);
    }

    @Deprecated
    public VCompletableFuture<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        return exceptionallyCompose(fn);
    }

    public CompletableFuture<T> toCompletableFuture() {
        return completableFuture;
    }

    public String toString() {
        return completableFuture.toString();
    }
}