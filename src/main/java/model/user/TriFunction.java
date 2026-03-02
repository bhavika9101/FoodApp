package model.user;

@FunctionalInterface
public interface TriFunction<U, P, N, R> {
    R apply(U u, P p, N n);
}
