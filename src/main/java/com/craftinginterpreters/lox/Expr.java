package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expr {
  interface Operation<R> {
    R onBinaryExpr(Binary expr);
    R onGroupingExpr(Grouping expr);
    R onLiteralExpr(Literal expr);
    R onUnaryExpr(Unary expr);
  }

  abstract <R> R execute(Operation<R> operation);

  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R execute(Operation<R> operation) {
      return operation.onBinaryExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R execute(Operation<R> operation) {
      return operation.onGroupingExpr(this);
    }

    final Expr expression;
  }

  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R execute(Operation<R> operation) {
      return operation.onLiteralExpr(this);
    }

    final Object value;
  }

  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R execute(Operation<R> operation) {
      return operation.onUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }
}
