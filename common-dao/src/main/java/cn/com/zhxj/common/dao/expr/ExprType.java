package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

public class ExprType {


    public static final ExprType Constant   = new ExprType(ExprTypeName.Constant  );
    public static final ExprType From       = new ExprType(ExprTypeName.From      );
    public static final ExprType Query      = new ExprType(ExprTypeName.Query     );
    public static final ExprType Insert     = new ExprType(ExprTypeName.Insert    );
    public static final ExprType Update     = new ExprType(ExprTypeName.Update    );
    public static final ExprType Delete     = new ExprType(ExprTypeName.Delete    );
    public static final ExprType FieldRef   = new ExprType(ExprTypeName.FieldRef  );
    public static final ExprType TableRef   = new ExprType(ExprTypeName.TableRef  );
    public static final ExprType GroupBy    = new ExprType(ExprTypeName.GroupBy   );
    public static final ExprType Select     = new ExprType(ExprTypeName.Select    );
    public static final ExprType SelectEntry     = new ExprType(ExprTypeName.SelectEntry    );
    public static final ExprType Where      = new ExprType(ExprTypeName.Where     );
    public static final ExprType Sql        = new ExprType(ExprTypeName.Sql       );


    @Getter
    private final String typeName;

    public ExprType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 逻辑表达式
     */
    public static class LogicalType extends ExprType {
        public static final LogicalType And = new LogicalType(ExprTypeName.And);
        public static final LogicalType Or  = new LogicalType(ExprTypeName.Or);
        public static final LogicalType Not = new LogicalType(ExprTypeName.Not);

        public LogicalType(String typeName) {
            super(typeName);
        }
    }

    /**
     * 二元表达式
     */
    public static class BinaryType extends ExprType {
        public static final BinaryType Gt         = new BinaryType(ExprTypeName.Gt);
        public static final BinaryType Gte        = new BinaryType(ExprTypeName.Gte);
        public static final BinaryType Lt         = new BinaryType(ExprTypeName.Lt);
        public static final BinaryType Lte        = new BinaryType(ExprTypeName.Lte);
        public static final BinaryType Eq         = new BinaryType(ExprTypeName.Eq);
        public static final BinaryType NotEq      = new BinaryType(ExprTypeName.NotEq);
        public static final BinaryType Like       = new BinaryType(ExprTypeName.Like);
        public static final BinaryType StartWith  = new BinaryType(ExprTypeName.StartWith);
        public static final BinaryType EndWith    = new BinaryType(ExprTypeName.EndWith);
        public static final BinaryType Contain    = new BinaryType(ExprTypeName.Contain);
        public static final BinaryType NotLike    = new BinaryType(ExprTypeName.NotLike);
        public static final BinaryType Between    = new BinaryType(ExprTypeName.Between);
        public static final BinaryType NotBetween = new BinaryType(ExprTypeName.NotBetween);
        public static final BinaryType In         = new BinaryType(ExprTypeName.In);
        public static final BinaryType NotIn      = new BinaryType(ExprTypeName.NotIn);
        public static final BinaryType Add        = new BinaryType(ExprTypeName.Add);
        public static final BinaryType Sub        = new BinaryType(ExprTypeName.Sub);
        public static final BinaryType Mul        = new BinaryType(ExprTypeName.Mul);
        public static final BinaryType Div        = new BinaryType(ExprTypeName.Div);
//        public static final BinaryType Mod        = new BinaryType(ExprTypeName.Mod);

        public BinaryType(String typeName) {
            super(typeName);
        }
    }

    /**
     * 一元表达式
     */
    public static class UnaryType extends ExprType {
        public static final UnaryType IsNull    = new UnaryType(ExprTypeName.IsNull);
        public static final UnaryType IsNotNull = new UnaryType(ExprTypeName.IsNotNull);
        public static final UnaryType Negative  = new UnaryType(ExprTypeName.Negative);

        public UnaryType(String typeName) {
            super(typeName);
        }
    }

    public static class OrderEntryType extends ExprType {
        public static final OrderEntryType Asc  = new OrderEntryType(ExprTypeName.Asc);
        public static final OrderEntryType Desc = new OrderEntryType(ExprTypeName.Desc);

        public OrderEntryType(String typeName) {
            super(typeName);
        }
    }

    public static class OrderByType extends ExprType {
        public static final OrderByType OrderBy = new OrderByType(ExprTypeName.OrderBy);

        public OrderByType(String typeName) {
            super(typeName);
        }
    }


    public static class MethodCallType extends ExprType {
        public static final MethodCallType MethodCall    = new MethodCallType(ExprTypeName.MethodCall);

        public MethodCallType(String typeName) {
            super(typeName);
        }
    }
}
