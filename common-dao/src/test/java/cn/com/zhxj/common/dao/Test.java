package cn.com.zhxj.common.dao;



import lombok.SneakyThrows;


public class Test {

//    private static DefaultDaoImpl<TdCarbill> dao = new DefaultDaoImpl<>(TdCarbill.class);

    @SneakyThrows
    public static void main(String[] args) {
//        new DefaultDaoImpl<TdCarbill>()
//                .findWheres(w->
//                        w.where(TdCarbill::getFd_code,eq(),"xiaojun")
//                ).selectFields(TdCarbill::getFd_code)


//        int row = dao.delete(w -> $(w.getFd_code(), eq(), "xiaojun"));
//        dao.delete(w->$(  $($(w.getFd_id(),gt(),100) ,and() ,$(w.getFd_id(),eq(),100)  ))


//        List<TdCarbill> orders = dao.findByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//        Optional<TdCarbill> order1= dao.findFirstByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//
//        List<Traced<TdCarbill>> list = dao.findForUpdate(w -> {
//            w.setFd_code("xx");
//        });
//
//        TdCarbill tdCarbill1 = list.get(0).get();
//        tdCarbill1.setFd_date(new Date());       List<TdCarbill> orders = dao.findByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//        Optional<TdCarbill> order1= dao.findFirstByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//
//        List<Traced<TdCarbill>> list = dao.findForUpdate(w -> {
//            w.setFd_code("xx");
//        });
//
//        TdCarbill tdCarbill1 = list.get(0).get();
//        tdCarbill1.setFd_date(new Date());       List<TdCarbill> orders = dao.findByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//        Optional<TdCarbill> order1= dao.findFirstByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//
//        List<Traced<TdCarbill>> list = dao.findForUpdate(w -> {
//            w.setFd_code("xx");
//        });
//
//        TdCarbill tdCarbill1 = list.get(0).get();
//        tdCarbill1.setFd_date(new Date());       List<TdCarbill> orders = dao.findByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//        Optional<TdCarbill> order1= dao.findFirstByExample(w -> {
//            w.setFd_code("xiaojun");
//        });
//
//
//        List<Traced<TdCarbill>> list = dao.findForUpdate(w -> {
//            w.setFd_code("xx");
//        });
//
//        TdCarbill tdCarbill1 = list.get(0).get();
//        tdCarbill1.setFd_date(new Date());

//        dao.update(list.get(0));

//        dao.find(w->w.getFd_code() in() (dao.find(w->w.getFd_code() )))


        String name = "xisdo" + Math.random();

//        SerializedLambda
//        EntityConsumer<TdCarbill> getFd_code = (w) -> {};
//
//        Method m = getFd_code.getClass().getDeclaredMethod("writeReplace");
//        m.setAccessible(true);
//        SerializedLambda sl = (SerializedLambda) m.invoke(getFd_code);
//
//
////
//         getFd_code.accept(new TdCarbill());
//
//        Class<Test> testClass = Test.class;
//
//
//        Long orderId=null;
//        Long orderId2=null;
//        String orderCode=null;
//
//
//
//        List<TdCarbill> orders = new DefaultDaoImpl<TdCarbill>().findWhere(w -> {
//            w.ifNotNullAnd(orderId, TdCarbill::getFd_code, in(),orderId,orderId2)
//                    .ifNotBlankOr(orderCode, TdCarbill::getFd_code, eq(), orderCode)
//                    .ifNotBlankOr(orderCode, TdCarbill::getFd_code, eq(), orderCode);
//        }).selects(w -> {
//            w.setFd_code(null);
//            w.setFd_date(null);
//            w.setFd_id(null);
//        }).select(TdCarbill::getFd_id).toList();

//        ExprBuilder<TdCarbill> tdCarbillExprBuilder = new ExprBuilder<>();

//        LogicalExpr orderId = $(gt(TdCarbill::getFd_id, 100), and(), gt(TdCarbill::getFd_id, 100));
//        LogicalExpr orderCode = $(gt(TdCarbill::getFd_code, 100), and(), gt(TdCarbill::getFd_code, 100));
//        Expr orderId2 = $notBlank("", gt(TdCarbill::getFd_code, 100));
//        LogicalExpr logicalExpr = $(orderId, or(), $(orderCode, or(),orderId2));


//        TdCarbill w = new TdCarbill();
//        LogicalExpr expr = $(
//                $notBlank("", $(w.getFd_code(), eq(), "")),
//                and(),
//                $(
//                        $(w.getFd_id(), gt(), 110),
//                        or(),
//                        $(w.getFd_code(), isNotNull())
//                )
//        );


//        TdCarbill ord = new TdCarbill();
//
//        BinaryExpr orderIdExpr = $(ord.getFd_id(), eq(), "CO2013220"); //二元表达式
//        UnaryExpr orderIdIsNull = $(ord.getFd_id(), isNotNull());  //一元表达式
//        LogicalExpr where = $(orderIdExpr, and(), orderIdIsNull); //逻辑表达式
//
//        DefaultDaoImpl<TdCarbill> tsd = new DefaultDaoImpl<TdCarbill>(entityClass)
//                .where(w -> $(
//                        $(w.getFd_code(), eq(), ""),
//                        and(),
//                        $(
//                                $(w.getFd_id(), gt(), 110),
//                                or(),
//                                $(w.getFd_code(), isNotNull())
//                        )
//                ));
//
//        new DefaultDaoImpl<TdCarbill>(entityClass)
//                .where(w->$(w.getFd_code(),GTE,""))
//                .where(w->$(w.getFd_id(),gt(),100))
//                .orderBys(w->$($(w.getFd_code(),ASC),$(w.getFd_id(),desc())))
//                .orderBy(w->$(w.getFd_id(),ASC))
//                .selectExclude(w->w.getId())


//        Consumer

//        List<TdCarbill> sdsd = new DefaultDaoImpl<TdCarbill>(TdCarbill.class).findPage(
//                w -> $(w.getFd_id(), lt(),1000L),
//                w-> $($(w.getFd_id(),desc())),2 ,50);

//        Obj obj = new Obj();
//        obj.setFd_id("xiaojun");
//        obj.setThat(obj);
//        EntityFunction<TdCarbill, Expr> t2 =w->$(w.getFd_id(),eq(),"xsds");
//        LambdaInfo lambdaInfo = ReflectionUtils.getLambdaInfo(t2);
//        TdCarbill o = ProxyUtils.newProxyObject(TdCarbill.class);
//        o.setFd_date(new Date());
//
//        List<PropertyCall> executedProps = ProxyUtils.getExecutedProps(null,false,false);

//        Expr apply = t2.apply(o);
//        System.out.println("");

        //删除
//        dao.delete(w -> $(w.getFd_id(), eq(), 100));
//        dao.delete(w -> $($(w.getFd_id(), eq(), 100) ,or(),$(w.getFd_code(),isNotNull())));
//
//        List<TdCarbill> list = dao.findByExample(w -> {
//            w.setFd_id(100L);
//        });
//
//        List<TdCarbill> xsd = dao.findFirstByExample(w -> w.setFd_code("xsd"));
//
//        int xiaojun = dao.deleteByExample(w -> {
//            w.setFd_code("xiaojun");
//        });


        //   一元
        // a is null
        // a > b   a= b
        // a and() b


        // a > b
        //
        // up

//        new BinaryExpr(new FieldRefExpr("xxx"),and(), new ConstantExpr("100"))
//
//
//        //select
////
////        Traced<TdCarbill> tracedEntity = new Traced<>(TdCarbill.class);
////        TdCarbill tdCarbill = tracedEntity.get();
//
//        TdCarbill tdCarbill = new TdCarbill();
//        tdCarbill.setFd_code("小军");
//        tdCarbill.setFd_id(-100L);
//
//        TdCarbill o = ProxyUtils.newTracedObject(tdCarbill);
//        System.out.println(tdCarbill.getFd_code());
//        o.setFd_code("xxxx");
//
//        Traced<TdCarbill> traced = Traced.startTrace(new TdCarbill());
//        traced.get().setFd_code("xiaojun");
//
//
////        List<TdCarbill> list = dao.findPage(w -> $(w.getFd_id(), eq(), ""),w->w.getFd_id());
//
//        System.out.println("xx");

//
//        TdCarbill order = new TdCarbill();
//        order.setFd_id(100L);
//        order.setFd_code("");
//        order.setFd_date(null);
//        List<TdCarbill> list = dao.findExample(order, ExamplePolicy.INCLUDE_NOT_NULL);  // where fd_id =100 and fd_code='' , 不包含 null值
//        List<TdCarbill> list2 = dao.findExample(order, ExamplePolicy.EXCLUDE_NULL_EMPTY);  // where fd_id =100, 不包含 null值 和 empty字符串
//        List<TdCarbill> list3 = dao.findExample(order, ExamplePolicy.EXCLUDE_NULL_EMPTY_BLANK);  // where fd_id =100 , 不包含 null值 和 empty,blank字符串
//
//        DefaultDaoImpl.Queryable<TdCarbill> query = dao.query();
//        query=query
//                .where(w-> w.setFd_code("xiaojun"))   // where fd_code = xiaojun
//                .where(w->{
//                    w.setFd_id(100L);
//                    w.setFd_code(null);
//                })    // and fd_id = 100 and fd_code is null
//                .orderByAsc(w-> $list(w.getFd_id(),w.getFd_code()))   // order By  fd_id desc
//                .orderByDesc(w-> $list(w.getFd_date()))
//                .orderByMap(w->{
//                    w.put("fd_id","asc");  // order By  fd_id asc
//                    w.put("fd_date","desc");  // order By  fd_date desc
//                })
//                .unsafeOrderByMap(w->{
//                    w.put("fd_id+100","desc");  // order By  fd_id+100 asc   不安全的order By 字符串作为sql的一部分
//                });
//
//        List<TdCarbill> tdCarbills = query.toList();  //查询列表
//        Paged<TdCarbill> paged = query.toPage(1, 10); // 分页
//        List<TdCarbill> top = query.top(2);   // rownum<=2   前两行
//        TdCarbill first = query.first(); // rownum<=1  前1行
//
//
//        DefaultDaoImpl.Queryable<TdCarbill> query2 = dao.query();
//        query2= query2.whereExpr(w->{
//            UnaryExpr orderCode = $(w.getFd_code(), isNotNull());   // where fd_code is not null
//            BinaryExpr orderId = $(w.getFd_id(), gt(), 100L);  // where fd_id > 100
//            return $(orderCode, and(),orderId);    // where fd_code is not null and fd_id > 100
//        });
//
//        query2= query2.select(w->Arrays.asList(w.getFd_id()));  // select FD_CODE FROM xx  select 部分字段
//
//        Long count = query2.count();  // 聚合函数 select count(*) from xxx
//        BigDecimal $avg = query2.$avg();//
//        boolean exists = query2.exists();  //是否存在
//
//        DefaultDaoImpl.Queryable<TdCarbill> tdCarbillQuery = new DefaultDaoImpl<>(TdCarbill.class).query();
//        DefaultDaoImpl.Queryable<TdAgreement> tdagreementQuery = new DefaultDaoImpl<>(TdAgreement.class).query();
//        DefaultDaoImpl.Queryable<TdAgreement> tdagreements = tdagreementQuery.where(s -> s.setFd_date(null)).select(w->Arrays.asList(w.getFd_id()));
//
//        tdCarbillQuery.whereExpr(w->$(w.getFd_id(),in(),tdagreements)); // in 子句 查询 :  select * from td_carbill where fd_id in ( select fd_id from td_agreement )
//
//
//        TdCarbill o = new TdCarbill();
//        Tuple3<String, Long, Date> tuple3 = Tuple.of(o.getFd_code(), o.getFd_id(), o.getFd_date());
    }



}
