## 简介
一个类型安全，简单高效的数据库访问框架，支持多个数据库 
不需要xml 
可以实现单表大部分常用操作，可以搭配mybaits使用

## Spring boot demo

```
  @SpringBootApplication
  @EnableCommonDao
  @EnableTransactionManagement
  public class SpringBootTestApplication {
      
      @Autowired
      private Dao<TdCompany> dao;
      
      public static void main(String[] args) throws ClassNotFoundException {
          SpringApplication.run(SpringBootTestApplication.class, args);
      }

     @Bean
      public SqlDriverHolder sqlDriverHolder(JdbcTemplate jdbcTemplate){
         return new SqlDriverHolder() {
           @Override
           public <T extends Entity> SqlDriver getSqlDriver(EntityDesc<T> entityDesc, SqlCmdType sqlType) {
              return SqlDriver.getByJdbcTemplate(jdbcTemplate);
           }
         };
      }

       @PostConstruct
       public void demo()  {
         //查询demo
         Paged<TdCompany> page = companyDao.query()
                 .whereExpr(w -> $(w.getFd_id(), in(), ids)) // where 表达式
                 .where(w->w.setFd_del(0))  //where
                 .whereNotBlank("predicate string",w-> $(w.getFd_phone(),isNotNull()))  //只有条件值不为 blank或者null 才会应用 where条件
                 .whereIdEqual(100L)   //where id = 100
                 .whereExample(new TdCompany(),ExamplePolicy.EXCLUDE_NULL_EMPTY) // example查询，排除 null/empty值
                 .whereExampleExclude(new TdCompany(),ExamplePolicy.EXCLUDE_NULL_EMPTY,w->list(w.getFd_del()))  // example查询，排除 null/empty值, 并且不包含指定的字段
                 .whereMap(w->{
                     w.put("id",100L);   //字段名需要区分 大小写，跟java字段名一致
                     w.put("FD_ERPCODE","121"); //表列名不需要区分大小写
                 })
                 .orderByAsc(w-> list(w.getFd_id())) //排序
                 .orderByFields(w->list(w.getFd_del(),w.getFd_erpcode()),false) //排序
          //     .toList()    //查询列表
          //     .toList(10) //查询前10行
          //     .first()     //查询第一行
          //     .toTuple1(w->tuple(w.getFd_id()))  //查一个字段
          //     .toTuple2(w->tuple(w.getFd_id(),w.getFd_erpcode())) //查询两个字段
          //     .toPageCountLess(3,3)  //分页查询，但是不进行count
          //     .toPageHLLP(3,3)  //分页查询，用 基数算法 估算 行数，仅在海量数据的时候适用，精度差，勿使用
          //     .toDistinctMap(w->tuple(w.getFd_id(),w.getFd_erpcode()))   // 返回map，第一个字段作为key，第二个字段作为value， select distinct fd_id,fd_erpcode from xxx,
          //     .top(10)   //查询前10行，  跟 toList(10)  一样
          //     .count()  //计算行数， select count(*) from xxx
          //     .sum(w->tuple(w.getFd_id()))  //sum计算， select sum(fd_id) from xxx
          //     .avg(w->tuple(w.getFd_id()))  //avg计算， select avg(fd_id) from xxx
          //     .max(w->tuple(w.getFd_id()))  //max计算， select max(fd_id) from xxx
          //     .min(w->tuple(w.getFd_id()))  //min计算， select min(fd_id) from xxx
          //     .distinct(w->list(w.getFd_id(),w.getFd_erpcode())) //select distinct fd_id,fd_erpcode from xxx
          //     .countDistinct(w->list(w.getFd_id(),w.getFd_erpcode())) // select count(distinct fd_id,fd_erpcode) from xxx
          //     .exists()   //返回bool ，检查是否存在数据
          //     .select(w->list(w.getFd_id(),w.getFd_del()))    // 只select 两个字段
          //     .selectExclude(w->list(w.getFd_id()))             // select 的时候排除指定字段
          //     .toStream()                               // 返回Stream<T> 对象
          //     .selectOneField(TdCompany::getFd_erpcode)    //只select 一个字段
          //     .toListForUpdate(10)                          //返回List<Traced<T>> ,用于 update操作, Traced<T> 会跟踪字段的更改
          //     .firstForUpdate()                             //返回 第一个 Traced<T> 用于update操作
                 .toPage(3, 3);  //分页查询
                 
                 
           //动态where 表达式 
          companyDao.query()
                     .whereExpr(w->{
                         BinaryExpr where = $(1, eq(), 1); // 
                         if(areaVo.getId()!=null){
                             where=$(where,and(),$(w.getId(),eq(),areaVo.getId()));
                         }
                         if(areaVo.getNameLike()!=null){
                             where=$(where,and(),$(w.getName(),like(),"%"+areaVo.getNameLike()+"%"));
                         }
                         if(areaVo.getIdIn()!=null){
                             where=$(where,and(),$(w.getId(),in(),areaVo.getIdIn()));
                         }
                         return where;
                     })
                     .orderByExpr(w->$(w.getId(),desc(),w.getDel(),asc()))   // order by fd_id desc,fd_del asc
                     .toPage(areaVo.getPage(),areaVo.getPageSize()); //分页sql    
                     
          // insert
          TdCompany tdCompany = new TdCompany();
          tdCompany.setFd_phone("xx");
          tdCompany.setFd_del(0);
          int row = companyDao.insert(tdCompany);
          
          
          //update
          dao.updateDynamic(w->w.setStatus(1),w->w.setId(100))   //update xxx set FD_STATUS=1 where FD_ID=100
          dao.updateDynamicById(w->w.setStatus(1),100)   //update xxx set FD_STATUS=1 where FD_ID=100

          TdCompany cp=new TdCompany();
          cp.setName("xxxx")
          cp.setErpCode(null);
          
          dao.updateNotNull(cp,w->w.setId(100)) // update xxx set FD_NAME='xxxxx' where fd_id= 100
          dao.updateNotNullById(cp,100) // update xxx set FD_NAME='xxxxx' where fd_id= 100
          dao.updateNotNullWhere(cp,w->$(w.getId(),eq(),1000)) // update xxx set FD_NAME='xxxxx' where fd_id= 100
 
          int row = companyDao.update()
                     .setIf(StringUtils.isNotBlank(phone),w->{
                         w.setFd_phone(phone);
                     })
                     .whereExpr(w-> $(
                            $(w.getFd_id(),eq(),100L) ,
                            and() ,
                            $(w.getFd_del(),eq(), 1L)
                     )).execute();

         //delete
         dao.delete(w->{
           w.setId(100);
           w.setDel(0);
         })  // delete from xxx where id=100 and del=0
         dao.deleteBy(TdCompany::getId,1000)  delete from xxx where id=100
         dao.deleteById(1000)  delete from xxx where id=100
         dao.deleteWhere(w->$(w.getId(),eq(),1000))  // delete from xx where id=1000
                       
       }      
    } 
    
     @Data
        @Table(name = "TD_COMPANY")
        public class TdCompany  {
            @Id
            @Column(name = "FD_ID")
            @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "SEQ_TD_COMPANY")
            @SequenceGenerator(name = "SEQ_TD_COMPANY",sequenceName = "SEQ_TEST")
            private Long   fd_id;
            @Column(name = "FD_PHONE")
            private String fd_phone;
            @Column(name = "FD_ERPCODE")
            private String fd_erpcode;
            @Column(name = "FD_DEL")
            private Integer fd_del;
             @InsertTime
             @Column(name = "FD_CREATETIME")
             private Date   fd_createtime;
             @Column(name = "FD_NAME")
             private String fd_name;
        }              
```


## 数据库兼容性

### oracle

支持，支持common-dao的所有能力

### mysql

支持，支持common-dao的所有能力，未充分测试

### 其他数据库

支持，支持common-dao的部分能力，不支持分页，limit查询

## 代码生成器

### 实体

使用说明

1.  将CommonDaoEntity.groovy复制到 IDEA的 “Scratches and Consoles”/Extentions/Database Tool and Sql/schema
2.  打开IDEA的Database工具栏，连接数据库
3.  选择需要生成的表，生成实体代码


### 实体代码demo

实体代码应该与表结构一致，尽可能不要再实体类里面添加无关字段和代码
```
@Data
@Table(name = "RULE_PRICE_VIPCHANNEL")
public class RulePriceVipchannel implements Entity {

	/**
	 * 编号
	 */
	@Id
	@Column(name = "FD_ID")
	@IdUtil
	private String id;

	/**
	 * 币种
	 */
	@Column(name = "FD_COIN_TYPE")
	private String coinType;

	/**
	 * 用户姓名
	 */
	@Column(name = "FD_CREATOR")
	private String creator;

	/**
	 * 创建时间
	 */
	@Column(name = "FD_CREATE_TIME")
	@InsertTime
	private Date createTime;

	/**
	 * 最后更新人姓名
	 */
	@Column(name = "FD_UPDATOR")
	private String updator;

	/**
	 * 最后更新时间
	 */
	@Column(name = "FD_UPDATE_TIME")
	@UpdateTime
	private Date updateTime;

	/**
	 * 网点清单 对应不同产品的code
	 */
	@Column(name = "FD_NETWORK_CODES")
	private String networkCodes;

}
```
### 实体的继承

公用的字段可以放到父类里面，代码生成器不会自动处理继承关系，请生成代码后手工调整，如有需要，自行调整CommonDaoEntity.groovy代码生成器

## 字段注解

### JPA注解
```
 @Table 
 @Id
 @Transient 
 @Column 
 @GeneratedValue 
 @SequenceGenerator
```
### Mybait plus注解

不建议使用
```
@TableName 
@KeySequence 
@TableId 
@TableField
```

### 自定义注解
```
 @IdUtil
 @InsertTime
 @UpdateTime
```

## 新增

### 单条insert

#### Demo
```
Dao<TdCompany> dao=null;
TdCompany tdCompany = new TdCompany();
tdCompany.setFd_phone("xx");
tdCompany.setFd_del(0);
int row = dao.insert(tdCompany);
```

#### 方法重载
```

   /**
     * insert 1.id会回填
     * @param entity 
     * @return
     */
    int insert(T entity);

    /**
     * insert 1.id会回填
     * @param entity 
     * @return
     */
    int insert(Consumer<T> entity);

    /**
     * insert 1.id会回填, 只有不为null的字段才会insert,
     * 因此,null字段对应的数据库列必须是可为null值 或者 有默认值
     * @param entity 
     * @return
     */
    int insertNotNull(T entity);

    /**
     * insert 1.id会回填, 只有不为null的字段才会insert,
     * 因此,null字段对应的数据库列必须是可为null值 或者 有默认值
     * @param entity
     * @return
     */
    int insertNotNull(Consumer<T> entity);
```
  

### 批量insert
```
  /**
     * 批量insert,注意，批量insert不会回填由数据库生成的id
     * @param entities
     */
    void insertBatch(List<T> entities);

    /**
     * 批量insert,注意，批量insert不会回填由数据库生成的id
     * @param entities
     */
    void insertBatch(Consumer<List<T>> entities);
```
### 主键生成

主键默认情况会被回填，但是 批量insert 的时候，Oracle序列/自增ID是不会回填的

#### Oracle序列
```
    @Id
    @Column(name = "FD_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TD_CARBILL")
    @SequenceGenerator(name = "SEQ_TD_CARBILL", sequenceName = "SEQ_TD_CARBILL")
    private Long   fd_id;
```

#### 自增Id
```
    @Id
    @Column(name = "FD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   fd_id;
```
#### IdUtils
```
    @Id
    @Column(name = "FD_ID")
    @IdUtil
    private Long   fd_id;
```
### 创建时间

注意点

Insert的时候，@InsertTime插入数据库的时候实际上是 调用CURRENT_TIMESTAMP 或者 SYSDATE, 而回填给实体的值是new Date(),也就是说 回填的值 跟 插入到数据库的值是不相等的
```
    @InsertTime
    @Column(name = "FD_CREATETIME")
    private Date   fd_createtime;
```

## 修改

### 普通update

   见下面

### 批量update见
```
 void updateBatch(List<T> entities);
```

### 安全的update
```
   //只update部分字段 
  dao.updateDynamic(w->w.setStatus(1),w->w.setId(100))   //update xxx set FD_STATUS=1 where FD_ID=100
   dao.updateDynamicById(w->w.setStatus(1),100)   //update xxx set FD_STATUS=1 where FD_ID=100

 //只update不为null的字段
TdCompany cp=new TdCompany();
cp.setName("xxxx")
cp.setErpCode(null);

dao.updateNotNull(cp,w->w.setId(100)) // update xxx set FD_NAME='xxxxx' where fd_id= 100
dao.updateNotNullById(cp,100) // update xxx set FD_NAME='xxxxx' where fd_id= 100
dao.updateNotNullWhere(cp,w->$(w.getId(),eq(),1000)) // update xxx set FD_NAME='xxxxx' where fd_id= 100
```
### 链式update
```
Dao<TdCompany> companyDao=null;
Traced<TdCompany> traced = companyDao.findFirstForUpdate(w -> w.setFd_id(100L));
TdCompany company = traced.get();
company.setFd_erpcode("testerpcode");  //被修改的字段
int row = companyDao.update()
          .setModified(traced)         //对于没有修改的字段，将会被忽略
          .whereIdEqual(company.getFd_id())
          .execute();


Dao<TdCompany> companyDao = null;
TdCompany company = companyDao.query()
              .whereIdEqual(100L)
              .first();
company.setFd_erpcode("testerpcode");
int row = companyDao.update()
          .setAllField(company)         //所有字段(除了id外)都会被update,不管 值有没有被修改
          .whereExpr(w->$(w.getFd_id(),eq(),company.getFd_id()))
          .execute();


Dao<TdCompany> companyDao = null;
TdCompany company = companyDao.query()
              .whereIdEqual(100L)
              .first();
company.setFd_erpcode("testerpcode");
int row = companyDao.update()
          .setNotNull(company)
          .where(w->w.setFd_id(company.getFd_id()))
          .execute();


Dao<TdCompany> companyDao = null;
TdCompany company = companyDao.query()
              .whereIdEqual(100L)
              .first();
company.setFd_erpcode("testerpcode");  //将会被update
company.setFd_phone(""); //不会被update
int row = companyDao.update()
          .setNotEmpty(company)
          .where(w->w.setFd_id(company.getFd_id()))
          .execute();


Dao<TdCompany> companyDao = null;
TdCompany company = companyDao.query()
              .whereIdEqual(100L)
              .first();
company.setFd_erpcode("testerpcode");  //将会被update
company.setFd_phone("   "); //不会被update
int row = companyDao.update()
          .setNotBlank(company)
          .where(w->w.setFd_id(company.getFd_id()))
          .execute();


Dao<TdCompany> companyDao = null;
int row = companyDao.update()
          .set(w->{
              //只会update这3个字段
              w.setFd_erpcode("000");
              w.setFd_phone(null);  //将会update为null
              w.setFd_del(1L);
          })
          .where(w->w.setFd_id(company.getFd_id()))
          .execute();

Dao<TdCompany> companyDao = null;
String phone="xxxx";
int row = companyDao.update()
          .setIf(StringUtils.isNotBlank(phone),w->{
              w.setFd_phone(phone);
          })
          .where(w->w.setFd_id(company.getFd_id()))
          .execute();

 Dao<TdCompany> companyDao = null;
 Map map=new HashMap();
 map.put("fd_phone","xxx");
 int row = companyDao.update()
           .setFieldMap(map)
           .where(w->w.setFd_id(100L))
           .execute();

 Dao<TdCompany> companyDao = null;
 Map map=new HashMap();
 map.put("FD_PHONE","xxx");
 int row = companyDao.update()
           .setColumnMap(map)
           .where(w->w.setFd_id(100L))
           .execute();


 Dao<TdCompany> companyDao = null;
 int row = companyDao.update()
           .setOne(TdCompany::getFd_phone,"xxxx")
           .where(w->w.setFd_id(100L))
           .execute();

Dao<TdCompany> companyDao = null;
String phone="xxxx";
int row = companyDao.update()
          .setIf(StringUtils.isNotBlank(phone),w->{
              w.setFd_phone(phone);
          })
          .whereExpr(w-> $(
                 $(w.getFd_id(),eq(),100L) ,
                 and() ,
                 $(w.getFd_del(),eq(), 1L)
          )).execute();
```

### 跟踪实体的修改

Traced类提供对象跟踪能力，原理是 一个对象保存两份，一份是未修改之前的，一份是修改之后的， xxxForUpdate返回会直接返回Traced对象，也可以 直接调用 Traced.startTrace(cp);
```
Dao<TdCompany> companyDao=null;
Traced<TdCompany> traced = companyDao.findFirstForUpdate(w -> w.setFd_id(100L));
TdCompany company = traced.get();
company.setFd_erpcode("testerpcode");  //被修改的字段
int row = companyDao.update()
          .setModified(traced)         //对于没有修改的字段，将会被忽略
          .whereIdEqual(company.getFd_id())
          .execute();


TdCompany cp=new TdCompany();
Traced.startTrace(cp);  //Traced会克隆一份原始的实体对象，未修改的，当修改完毕之后会 将最新的对象 跟 原始对象进行对比，如有修改 就update
cp.setName('xxxx');
dao.updateTracedById(cp,100)

```
//Traced 类
```
/**
 * 跟踪对象的更改
 */
public class Traced<T extends Entity> {

    private T        dirtyEntity;
    private T        entity;

    @SuppressWarnings("unchecked")
    private Traced(T entity){
       this.dirtyEntity=(T)dirtyEntity.copy();
       this.entity=entity;
    }

    public static <T extends Entity> Traced<T>  startTrace(T entity){
        return new Traced<>(entity);
    }


    public T get(){
        return entity;
    }

    public Traced<T> apply(Consumer<T> consumer){
        consumer.accept(entity);
        return this;
    }

    public Map<String,Object> getModifies(){
     
    }
}
```
### 更新时间

注意点

Insert/Update的时候，@UpdateTime插入/更新数据库的时候实际上是 调用CURRENT_TIMESTAMP 或者 SYSDATE, 而回填给实体的值是new Date(),也就是说 回填的值 跟 插入到数据库的值是不相等的
```
    @UpdateTime
    @Column(name = "FD_CREATETIME")
    private Date   fd_updatetime;
```
### 部分方法说明
```
  //Dao接口  
  int updateTraced(Traced<T> traced, Consumer<T> where);

    int updateTracedById(Traced<T> traced, Object id);

    int updateWhere(Consumer<T> setFields, Function<T, Expr> where);

    int updateDynamicWhere(Consumer<T> entity, Function<T, Expr> where);

    int updateAllFieldWhere(T entity, Function<T, Expr> where);

    int updateNotNullWhere(T entity, Function<T, Expr> where);

    int updateNotNullById(T entity, Object id);

    int updateAllFieldById(T entity, Object id);

    int updateDynamic(Consumer<T> entity, Consumer<T> where);

    int updateDynamicById(Consumer<T> entity, Object id);

    int updateAllField(T entity, Consumer<T> where);

    int updateNotNull(T entity, Consumer<T> where);


 //Updatable接口
   Updatable<T> setModified(Traced<T> entity);

    Updatable<T> setAllField(T entity);

    Updatable<T> setNotNull(T entity);

    Updatable<T> setNotEmpty(T entity);

    Updatable<T> setNotBlank(T entity);

    Updatable<T> set(Consumer<T> entity);

    Updatable<T> setIf(boolean predicate, Consumer<T> entity);

    Updatable<T> setNotNull(Object predicate, Consumer<T> entity);

    Updatable<T> setNotEmpty(String predicate, Consumer<T> entity);
    Updatable<T> setNotBlank(String predicate, Consumer<T> entity);

    Updatable<T> setFieldMap(Map<String, Object> fieldMapCaseSentive);

    Updatable<T> setFieldMap(Consumer<Map<String, Object>> colMapIgnoreCase);

    Updatable<T> setColumnMap(Map<String, Object> colMapIgnoreCase);

    default Updatable<T> setColumnMap(Consumer<Map<String, Object>> colMapIgnoreCase);

    <TField> Updatable<T> setOne(Function<T, TField> field, TField value);

    Updatable<T> where(Consumer<T> where);

    Updatable<T> whereIdEqual(Object id);

    Updatable<T> whereExpr(Function<T, Expr> where);

    int execute();
```

## 删除

### 普通删除
```
dao.delete(w->{
  w.setId(100);
  w.setDel(0);
})  // delete from xxx where id=100 and del=0

dao.deleteBy(TdCompany::getId,1000)  delete from xxx where id=100
dao.deleteById(1000)  delete from xxx where id=100
```
### 表达式删除
```
dao.deleteWhere(w->$(w.getId(),eq(),1000))  // delete from xx where id=1000
```
### 方法说明
```
 int deleteById(Object id);

 int deleteWhere(Function<T, Expr> where);

 int delete(Consumer<T> example);

<TField> int deleteBy(Function<T, TField> singleField, TField value);
```
## 自定义注解

### 内置注解

#### @IdUtil

insert的时候起作用，如果id值不为null，那就调用IdUtils.newId('') 方法生成ID并且回填

#### @InsertTime

insert的时候起作用，insert的时候字段值会使用数据库当前时间，但是实际回填的是new Date()

#### @UpdateTime

insert或者update的时候起作用，字段值会使用数据库当前时间，但是实际回填的是new Date()

### 创建自定义注解
```
  /**
 * 自定义注解，当前用户名
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser{
    Class insert = CurrenUserHandler.class;  //作用于 insert
   Class update= CurrenUserHandler.class;  //作用于 update
}


public class CurrenUserHandler implements ValueHandler {
    @Override
    public void process(FieldValueHolder holder) {
        //if(holder.getFieldValue()!=null) return; // 如果用户已经设置了用户名，那就不要覆盖了，这个要看具体情况来定
        holder.setFieldValue(getLoginUserName()); //设置字段值 为当前用户名, 回填到对象中
        //holder.setDbGenValue(DbGenValue.CURRENT_TIMESTAMP);  如果这个字段的值是由数据库生成的，那就设置这个值
    }
}
```
## 查询

### 普通查询
```
     Dao<TdCompany> dao=null;
//find
    List<TdCompany> list = dao.find(w -> {
      w.setFd_del(0);
      w.setFd_erpcode("xiaojun");
    });   // where fd_del=0 and fd_erpcode='xiaojun'

//findById
    TdCompany cp = dao.findById(1000); // @Id注解必须存在

//findExample
   TdCompany cp=new TdCompany();
   cp.setName("   ");
   cp.setErpCode("test");
   cp.setDel(null);
   List<TdCompany> list=dao.findExample(cp);  // where FD_ERPCODE='test' and FD_NAME='   '  //默认不包含null值
   List<TdCompany> list=dao.findExample(cp,ExamplePolicy.EXCLUDE_NULL_EMPTY);  // where FD_ERPCODE='test' and FD_NAME='   '  //默认不包含null值和 empty字符串
   List<TdCompany> list=dao.findExample(cp,ExamplePolicy.EXCLUDE_NULL_EMPTY_BLANK);  // where FD_ERPCODE='test'  //默认不包含null值和 empty/blank字符串
```
### 链式查询
```
 Dao<TdCompany> dao=null;
        Paged<TdCompany> page = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), in(), ids)) // where 表达式
                .where(w->w.setFd_del(0))  //where
                .whereNotBlank("predicate string",w-> $(w.getFd_phone(),isNotNull()))  //只有条件值不为 blank或者null 才会应用 where条件
                .whereIdEqual(100L)   //where id = 100
                .whereExample(new TdCompany(),ExamplePolicy.EXCLUDE_NULL_EMPTY) // example查询，排除 null/empty值
                .whereExampleExclude(new TdCompany(),ExamplePolicy.EXCLUDE_NULL_EMPTY,w->list(w.getFd_del()))  // example查询，排除 null/empty值, 并且不包含指定的字段
                .whereMap(w->{
                    w.put("id",100L);   //字段名需要区分 大小写，跟java字段名一致
                    w.put("FD_ERPCODE","121"); //表列名不需要区分大小写
                })
                .orderByAsc(w-> list(w.getFd_id())) //排序
                .orderByFields(w->list(w.getFd_del(),w.getFd_erpcode()),false) //排序
//                .toList()    //查询列表
//                .toList(10) //查询前10行
//                .first()     //查询第一行
//                .toTuple1(w->tuple(w.getFd_id()))  //查一个字段
//                .toTuple2(w->tuple(w.getFd_id(),w.getFd_erpcode())) //查询两个字段
//                .toPageCountLess(3,3)  //分页查询，但是不进行count
//                .toPageHLLP(3,3)  //分页查询，用 基数算法 估算 行数，仅在海量数据的时候适用，精度差，勿使用
//                .toDistinctMap(w->tuple(w.getFd_id(),w.getFd_erpcode()))   // 返回map，第一个字段作为key，第二个字段作为value， select distinct fd_id,fd_erpcode from xxx,
//                .top(10)   //查询前10行，  跟 toList(10)  一样
//                .count()  //计算行数， select count(*) from xxx
//                .sum(w->tuple(w.getFd_id()))  //sum计算， select sum(fd_id) from xxx
//                .avg(w->tuple(w.getFd_id()))  //avg计算， select avg(fd_id) from xxx
//                .max(w->tuple(w.getFd_id()))  //max计算， select max(fd_id) from xxx
//                .min(w->tuple(w.getFd_id()))  //min计算， select min(fd_id) from xxx
//                .distinct(w->list(w.getFd_id(),w.getFd_erpcode())) //select distinct fd_id,fd_erpcode from xxx
//                .countDistinct(w->list(w.getFd_id(),w.getFd_erpcode())) // select count(distinct fd_id,fd_erpcode) from xxx
//                .exists()   //返回bool ，检查是否存在数据
//                .select(w->list(w.getFd_id(),w.getFd_del()))    // 只select 两个字段
//                .selectExclude(w->list(w.getFd_id()))             // select 的时候排除指定字段
//                .toStream()                               // 返回Stream<T> 对象
//                .selectOneField(TdCompany::getFd_erpcode)    //只select 一个字段
//                .toListForUpdate(10)                          //返回List<Traced<T>> ,用于 update操作, Traced<T> 会跟踪字段的更改
//                .firstForUpdate()                             //返回 第一个 Traced<T> 用于update操作
                .toPage(3, 3);  //分页查询
```
### Limit查询

### 分页查询
```
  .toPage(3, 3);  //分页查询
  .toPageCountLess(3,3)  //分页查询，但是不进行count
  .toPageHLLP(3,3)  //分页查询，用 基数算法 估算 行数，仅在海量数据的时候适用，精度差，勿使用
```
### 动态Wehere

where添加不允许把字段值设为null，对于大多数数据库来说， where a=null 跟 where a is null 是两个完全不同的概念， 如果将字段值设为null，会导致无法查出任何数据

如果需要做 where a is null ,请使用 where表达式查询 whereExpr(w→$(w.getId(),isNull()))
```
     //动态where 表达式 
     areaDao.query()
                .whereExpr(w->{
                    BinaryExpr where = $(1, eq(), 1); // 
                    if(areaVo.getId()!=null){
                        where=$(where,and(),$(w.getId(),eq(),areaVo.getId()));
                    }
                    if(areaVo.getNameLike()!=null){
                        where=$(where,and(),$(w.getName(),like(),"%"+areaVo.getNameLike()+"%"));
                    }
                    if(areaVo.getIdIn()!=null){
                        where=$(where,and(),$(w.getId(),in(),areaVo.getIdIn()));
                    }
                    return where;
                })
                .orderByExpr(w->$(w.getId(),desc(),w.getDel(),asc()))   // order by fd_id desc,fd_del asc
                .toPage(areaVo.getPage(),areaVo.getPageSize()); //分页sql

   //动态where

          areaDao.query()
                .where(w->{
                    if (areaVo.getId()!=null){
                        w.setId(areaVo.getId()); // where fd_id =?
                    }
                    if (StringUtils.isNotBlank(areaVo.getCode())){
                        w.setCode(areaVo.getCode()); // where fd_id =?
                    }
                })
                .whereNotBlank(areaVo.getCodeLike(),w->$(w.getCode(),like(),"%"+areaVo.getCodeLike()+"%"))  // fd_code like ?
                .whereNotBlank(areaVo.getNameLike(),w->$(w.getName(),like(),"%"+areaVo.getNameLike()+"%")) //fd_name like ?
                .whereNotNull(areaVo.getIdIn(),w->$(w.getId(),in(),areaVo.getIdIn())) // fd_id in (????)
                .orderByExpr(w->$(w.getId(),desc(),w.getDel(),asc()))   // order by fd_id desc,fd_del asc
                .toPage(areaVo.getPage(),areaVo.getPageSize()); //分页sql
```
### Select

select只有在 调用部分方法的时候才会生效，比如 first(),toList(),toListForUpdate(),firstForUpdate(), 其他方法，select的字段会被覆盖掉，比如 toDisctinctMap()等

select里边目前只支持select字段，不支持字段加表达式混用，比如 .select(w→list( sum(w.getFd_id()) , $( w.getFd_del(), add() ,100 ) ))
```
                 .select(w->list(w.getFd_id(),w.getFd_del()))    // 只select 两个字段
                 .selectExclude(w->list(w.getFd_id()))             // select 的时候排除指定字段
                 .selectOneField(TdCompany::getFd_erpcode)    //只select 一个字段
```
### 元组
```
               .toTuple1(w->tuple(w.getFd_id()))  //查一个字段
               .toTuple2(w->tuple(w.getFd_id(),w.getFd_erpcode())) //查询两个字段


    //最多支持7个字段
    <T1> List<Tuple1<T1>> toTuple1(Function<T, Tuple1<T1>> tupleFunc);

    <T1, T2> List<Tuple2<T1, T2>> toTuple2(Function<T, Tuple2<T1, T2>> tupleFunc);

    <T1, T2, T3> List<Tuple3<T1, T2, T3>> toTuple3(Function<T, Tuple3<T1, T2, T3>> tupleFunc);

    <T1, T2, T3, T4> List<Tuple4<T1, T2, T3, T4>> toTuple4(Function<T, Tuple4<T1, T2, T3, T4>> tupleFunc);

    <T1, T2, T3, T4, T5> List<Tuple5<T1, T2, T3, T4, T5>> toTuple5(Function<T, Tuple5<T1, T2, T3, T4, T5>> tupleFunc);

    <T1, T2, T3, T4, T5, T6> List<Tuple6<T1, T2, T3, T4, T5, T6>> toTuple6(Function<T, Tuple6<T1, T2, T3, T4, T5, T6>> tupleFunc);

    <T1, T2, T3, T4, T5, T6, T7> List<Tuple7<T1, T2, T3, T4, T5, T6, T7>> toTuple7(Function<T, Tuple7<T1, T2, T3, T4, T5, T6, T7>> tupleFunc);


// Tuple类

public class Tuple2<T1, T2> extends Tuple {
    public T1 f1;
    public T2 f2;

    public Tuple2() {
    }

    public void setValue(int index, Object object) {
        if (object != null) {
            switch(index) {
            case 0:
                this.f1 = object;
                break;
            case 1:
                this.f2 = object;
                break;
            default:
                throw new IndexOutOfBoundsException("index out of bounds :" + index);
            }

        }
    }

    public T1 getF1() {
        return this.f1;
    }

    public T2 getF2() {
        return this.f2;
    }

    public void setF1(final T1 f1) {
        this.f1 = f1;
    }

    public void setF2(final T2 f2) {
        this.f2 = f2;
    }

    public Tuple2(final T1 f1, final T2 f2) {
        this.f1 = f1;
        this.f2 = f2;
    }
}
```
### Distinct
```
.countDistinct(w->list(w.getFd_id(),w.getFd_erpcode())) // select count(distinct fd_id,fd_erpcode) from xxx
.distinct(w->list(w.getFd_id(),w.getFd_erpcode())) //select distinct fd_id,fd_erpcode from xxx
.toDistinctMap(w->tuple(w.getFd_id(),w.getFd_erpcode()))   // 返回map，第一个字段作为key，第二个字段作为value， select distinct fd_id,fd_erpcode from xxx,
```
### 聚合函数查询
```
//                .count()  //计算行数， select count(*) from xxx
//                .sum(w->tuple(w.getFd_id()))  //sum计算， select sum(fd_id) from xxx
//                .avg(w->tuple(w.getFd_id()))  //avg计算， select avg(fd_id) from xxx
//                .max(w->tuple(w.getFd_id()))  //max计算， select max(fd_id) from xxx
//                .min(w->tuple(w.getFd_id()))  //min计算， select min(fd_id) from xxx
```
### 子查询
```
       /**
         * 实际生成的sql:
         *   select fd_id,fd_code from td_carbill  where fd_airportcode in (
         *         select fd_code from td_Destination where fd_cityid in (
         *           select fd_id from td_area where fd_code in (cityCodes)
         *         )
         *     )
         *
         */
        // select fd_id from td_area where fd_code in (cityCodes)
        Queryable<TdArea> cityIds = areaDao.query()
                .whereExpr(w -> $(w.getCode(), in(), cityCodes))
                .select(w->list(w.getId()));

        Queryable<TdDestination> airportCodes = destinationDao.query()
                .where(w -> $(w.getCityid(), in(), cityIds))
                .select(w -> list(w.getCode()));

        return   carbillDao.query()
                .whereExpr(w->$(w.getAirportcode(),in(),airportCodes))
                .toList(10);
```
### 关于findAll

目前common-dao是没有findAll方法的，findAll方法是一个非常危险的操作，如果确实需要 查询表所有数据

请使用whereExpr ， dao.whereExpr(w→$(w.getId(),eq(),w.getId())) 或者 dao.whereExpr(w→$(w.getId(),isNotNull()))

### 关于where的安全性

对于动态where,当所有条件都不满足的时候，就会出现where条件为空，而导致查全表的情况

因此，common-dao会在执行 sql前检查 where条件，如果where条件为空 或者 where条件不包含表字段引用 （比如 $(1,eq(),1)）， 就会直接报错

```     
 
//动态where 表达式 
     areaDao.query()
                .whereExpr(w->{
                    BinaryExpr where = $(1, eq(), 1); // 
                    if(areaVo.getId()!=null){
                        where=$(where,and(),$(w.getId(),eq(),areaVo.getId()));
                    }
                    if(areaVo.getNameLike()!=null){
                        where=$(where,and(),$(w.getName(),like(),"%"+areaVo.getNameLike()+"%"));
                    }
                    if(areaVo.getIdIn()!=null){
                        where=$(where,and(),$(w.getId(),in(),areaVo.getIdIn()));
                    }
                    return where;
                })
               

   //动态where

          areaDao.query()
                .where(w->{
                    if (areaVo.getId()!=null){
                        w.setId(areaVo.getId()); // where fd_id =?
                    }
                    if (StringUtils.isNotBlank(areaVo.getCode())){
                        w.setCode(areaVo.getCode()); // where fd_id =?
                    }
                })
 
```
## 排序

### Asc/Desc排序
```
Dao<TdCompany> dao=null;

List<TdCompany> list= dao.query()
   .where(w->w.setId(1L))
   .orderByDesc(w->list(w.getId(),w.getName()))  // order by FD_ID DESC,FD_NAME DESC
   .orderByAsc(w->list(w.getCode()))           // FD_CODE ASC
   .toList();  
```
### 表达式排序
```
Dao<TdCompany> dao=null;

List<TdCompany> list= dao.query()
   .where(w->w.setId(1L))
   .orderByExpr(w->$(w.getId(),desc(),w.getName(),asc(),w.getDel(), desc() ))  // order by FD_ID DESC, FD_DEL DESC
   .toList();  
```
### map排序
```
companyDao.query()
                .where(w -> w.setFd_id(companieIds\[0\]))
                orderByMap(w -> {
                    w.put("fd_id", "desc");   //key必须是 java字段名 或者 表列名，必须是实体类的一个字段或注解的列名，其中，字段名区分大小写，表列名不区分大小写
                })
```
### 不安全map排序
```
companyDao.query()
                .where(w -> w.setFd_id(companieIds\[0\]))
                .unsafeOrderByMap(w -> {
                    w.put("fd_id+1", "desc");   //这个是不安全的，key会作为sql的一部分，调用者需要保证不会被注入
                })
```
## 表达式

### 一元表达式

一元表达式有 ： is null / is not null
```
$(w.getId(),isNotNull())    // ID IS NOT NULL
$(w.getId(),isNull())      //ID IS NULL

  ```

### 二元表达式

二元表达式有 ： 大于/等于/小于/In/Not In...
```
$(w.getFd_phone(),like(),"%dsds%")    // LIKE
$(w.getFd_phone(),eq(),"dsds")        // 等于
$(w.getFd_id(),gt(),1000L)            //大于
$(w.getFd_id(),lte(),1000L)           //小于或等于
$(w.getFd_id(),in(),new Long\[\]{1000L,100001L})   //IN


//常用二元表达式
    public static BinaryType gt() {
        return BinaryType.Gt;
    }

    public static BinaryType gte() {
        return BinaryType.Gte;
    }

    public static BinaryType lt() {
        return BinaryType.Lt;
    }

    public static BinaryType lte() {
        return BinaryType.Lte;
    }

    public static BinaryType eq() {
        return BinaryType.Eq;
    }

    public static BinaryType notEq() {
        return BinaryType.NotEq;
    }

    public static BinaryType in() {
        return BinaryType.In;
    }

    public static BinaryType notIn() {
        return BinaryType.NotIn;
    }

    public static BinaryType like() {
        return BinaryType.Like;
    }

    public static BinaryType notLike() {
        return BinaryType.NotLike;
    }

    public static BinaryType between() {
        return BinaryType.Between;
    }

    public static BinaryType notBetween() {
        return BinaryType.NotBetween;
    }

    public static BinaryType add() {
        return BinaryType.Add;
    }

    public static BinaryType sub() {
        return BinaryType.Sub;
    }

    public static BinaryType mul() {
        return BinaryType.Mul;
    }

    public static BinaryType div() {
        return BinaryType.Div;
    }

    public static BinaryType startWith() {
        return BinaryType.StartWith;
    }

    public static BinaryType endWith() {
        return BinaryType.EndWith;
    }

    public static BinaryType contain() {
        return BinaryType.Contain;
    }
```
### 逻辑表达式

逻辑表达式有 : AND/OR
```
$($(w.getId,isNotNull()) , and() , $(w.getName(),eq(),'xxxx'))    //  ID IS NOT NULL AND NAME = 'xxxx'
$($(w.getId,isNotNull()) , or() , $(w.getName(),eq(),'xxxx'))    //  ID IS NOT NULL OR NAME = 'xxxx'

UnaryExpr erpCodeIsNotNull = $(w.getFd_erpcode(), isNotNull());
BinaryExpr phoneStartWith = $(w.getFd_phone(), startWith(), "137");
$(erpCodeIsNotNull,or(),phoneStartWith)                      // FD_ERPCODE IS NOT NULL OR FD_PHONE LIKE '137%'
```
### 数学运算符

数学运算符有：加减乘除 , 数学运算符目前仅适用于数字类型的字段，如果用于其他类型上，比如时间类型，会出现不同数据库 时间加减 处理 方法的不一致
```
$(w.getId(),add(),100L)    // ID 加 100
$(w.getId(),sub(),100L)    // ID 减 100

dao.findExpr(w->$(w.getFd_id(),eq(), $(w.getFd_id(),add(),1000L)))   // where FD_ID=FD_ID+1000

常用数学运算符

    public static BinaryType add() {
        return BinaryType.Add;
    }

    public static BinaryType sub() {
        return BinaryType.Sub;
    }

    public static BinaryType mul() {
        return BinaryType.Mul;
    }

    public static BinaryType div() {
        return BinaryType.Div;
    }
    public static BinaryType abs() {
        return BinaryType.Abs;
    }
```
### 聚合函数

暂时无法直接使用,请使用Queryable接口
```
    public static MethodCallExpr avg(Expr expr) ;

    public static MethodCallExpr sum(Expr expr) ;

    public static MethodCallExpr min(Expr expr) ;

    public static MethodCallExpr max(Expr expr) ;

    public static MethodCallExpr count();
```
### 字符串函数
```
dao.findExpr(w->$(lower(w.getFd_phone()),eq(),"12212"))     // WHERE LOWER(FD_PHONE)='12212'        转小写
dao.findExpr(w->$(replace(  lower(w.getFd_phone()) ,'a' ,'b' ),eq(),"12212"))     // WHERE REPLACE(LOWER(FD_PHONE),'a','b')='12212'        转小写 后替换
//常用字符串函数
    public static MethodCallExpr upper(String field) {}   //转大写
    public static MethodCallExpr lower(String field) {}   //转小写
    public static MethodCallExpr replace(String field,String replaceStr,String replaceTo) {}  // 替换
    public static MethodCallExpr concat(String... fields) {}   //字符串连接
    public static MethodCallExpr len(Expr expr) {}      //字符串长度
```
### 时间函数

  
```
dao.findExpr(w-> $(  w.getUpdateTime()  ,   gt()   ,  currentTimestamp() ))   // where FD_UPDATETIME > CURRENT_TIMESTAMP


 /**
     *  数据库当前时间 unit timestamp  oracle/mysql/mssql的 timestamp 类型
     * @return
     */
    public static MethodCallExpr currentTimestamp() { }  //当前时间

    /**
     *  数据库当前时间 DateTime  oracle/mysql/mssql的 DateTime 类型
     * @return
     */
    public static MethodCallExpr currentDateTime() {}   //当前时间
```
### 其他非标准函数

不建议使用，这些函数没有做多数据库适配
```
dao.findExpr(w-> $(  call("DECODE",w.getFd_name(),'xiaojun','1','2')  ,   eq()   ,   '2'  ))   // where DECODE(FD_NAME,'xiaojun','1','2')  = '2'
//非标准函数

public static MethodCallExpr call(String methodName,Object... args);
```
## 表达式alias

### 将表达式关联到对象上

待补充.....

## 直接使用表达式对象

### 表达式类型
```
BinaryExpr         //二元表达式, 便捷方法 $binary(1L)
ConstantExpr   //常量表达式, 便捷方法 $const(1L)
DeleteExpr      //DELETE表达式
Expr                //所有表达式的父类
ExprType        //表达式类型
ExprTypeName //表达式名称
FieldRefExpr    //字段引用 ， 便捷方法 $field("FD_NAME")
FromExpr       //FROM表达式  ，便捷方法 $from("TD_COMPANY")
InsertExpr     //INSERT表达式
LogicalExpr    //逻辑表达式 ，便捷方法 $logical()
MethodCallExpr //sql方法调用表达式
OrderByExpr    //排序表达式 
OrderEntryExpr //排序表达式项
QueryExpr      //查询表达式 ，便捷方法 $query()
SelectEntryExpr  //select表达式项
SelectExpr       //select表达式
SqlExpr          //sql表达式,直接作为sql的一部分 
StandardMethod   //枚举类，标识一些常用的sql标准方法
TableRefExpr     //table引用表达式 , $table()
UnaryExpr        //一元表达式 $unary()
UpdateExpr      // update表达式
WhereExpr      //where表达式
```
  

## 适配其他数据库或SQL语法

### 基于AnsiSqlVisitor

AnsiSqlVisitor已经实现了大部分兼容的SQL语法，继承与AnsiSqlVisitor 的只需要适配部分特殊的语法，比如分页/Limit
```
public class OracleVisitor extends AnsiSqlVisitor {

    @Override
    protected void processPagedQuery(QueryExpr expr) {
        Pagination pagination = expr.getPagination();

        int start = pagination.getStartInclude();
        int end = pagination.getEndExclude();
        String pageSql="SELECT * FROM (SELECT ROWNUM R_NUM,T_P.* FROM ({0}) T_P WHERE ROWNUM<={1} ) WHERE R_NUM>{2}";

        String sql = getSql();
        replaceSql(MessageFormat.format(pageSql,sql,end+"",start+""));
    }

    @Override
    protected void processLimitedQuery(QueryExpr expr) {
        Limited limited = expr.getLimited();
        String limitedSql="SELECT T_L.* FROM ({0}) T_L WHERE ROWNUM<={1}";
        String sql = getSql();
        replaceSql(MessageFormat.format(limitedSql,sql,limited.getLimit()+""));
    }

    @Override
    protected Expr visitMethodCall(MethodCallExpr expr) {
        if(expr.getStandardMethod()!=null){
            switch (expr.getStandardMethod()){
                case CurrentDateTime: appendScope("SYSDATE") ; return expr;
                case CurrentTimestamp: appendScope("CURRENT_TIMESTAMP") ; return expr;
            }
        }
        return super.visitMethodCall(expr);
    }


}
```
### 基于SqlVisitor

SqlVisitor 提供了 sql片段的开闭符号，分隔符，表别名，参数命名 的 一些基础方法
```
public class AnsiSqlVisitor extends SqlVisitor {


    @Override
    protected Expr visitLogical(LogicalExpr expr) {
        if (expr.getLeft() == null && expr.getRight() == null) {
            return expr;
        }
        if (expr.getLeft() == null) {
            return expr.getRight();
        }
        if (expr.getRight() == null) {
            return expr.getLeft();
        }
        beginScope("("," ",")");
        visit(expr.getLeft());
        String op = null;
        switch (expr.getType().getTypeName()) {
            case ExprTypeName.And:
                op = "AND";
                break;
            case ExprTypeName.Or:
                op = "OR";
                break;
            case ExprTypeName.Not:
                throw new IllegalArgumentException("不支持NOT");
        }
        appendScope(op);
        visit(expr.getRight());
        endScope();
        return expr;
    }

    @Override
    protected Expr visitConstant(ConstantExpr expr) {
        //a=? or c=? and $field=()
        Object data = expr.getData();
        if (data == null) {
            appendScope("NULL");
            return expr;
        }

        Expr aliasedExpr = ExprAliases.getAliasedExpr(data);
        if (aliasedExpr != null) {
            return aliasedExpr;
        }
        if (data instanceof List) {
            for (Object item : ((List) data)) {
                appendScope(":" + addParameter(item) + "");
            }
            return expr;
        }
        appendScope(":" + addParameter(data));
        return expr;
    }


    @Override
    protected Expr visitBinary(BinaryExpr expr) {
        if (expr.getLeft() == null && expr.getRight() == null) {
            return expr;
        }
        if (expr.getLeft() == null) {
            return expr.getRight();
        }
        if (expr.getRight() == null) {
            return expr.getLeft();
        }

        ExprType type = expr.getType();
        beginScope("("," ",")");
        visit(expr.getLeft());


        switch (type.getTypeName()) {
            case ExprTypeName.Between:
                beginScope("BETWEEN "," AND","");
                visit(expr.getRight());
                endScope();
                break;
            case ExprTypeName.NotBetween:
                beginScope("NOT BETWEEN "," AND","");
                visit(expr.getRight());
                endScope();
                break;
            case ExprTypeName.In:
                beginScope("IN ("," ,",")");
                visit(expr.getRight());
                endScope();
                break;
            case ExprTypeName.NotIn:
                beginScope("NOT IN ("," ,",")");
                visit(expr.getRight());
                endScope();
                break;
            default: {
                Expr right=expr.getRight();
                switch (type.getTypeName()) {
                    case ExprTypeName.Gt:
                        appendScope(">");
                        break;
                     //省略N行代码
                }
                visit(right);
                break;
            }
        }
        endScope();
        return expr;
    }

    @Override
    protected Expr visitFieldRef(FieldRefExpr expr) {
        appendScope(expr.getField());
        return expr;
    }
 //省略N行代码
}
```
### 基于ExprVisitor

只提供了遍历表达式树的能力
```
public class AnsiSqlVisitor extends ExprVisitor{
 //省略N行代码
}
```

## 适配其他映射框架

### 适配mybaits-plus注解
```
@Slf4j
public class MybaitsPlusEntityMapping<T extends Entity> implements EntityMapping<T> {
  @Override
  public EntityDesc<T> getEntityDesc(Class<T> tClass) {
      return init(tClass);
  }
}

  ```

### sqlDriverHolder配置spring集成
```
启用common-dao

@SpringBootApplication
@EnableCommonDao    //启用CommonDao
public class SpringBootTestApplication {}
```
### 读写分离

### 多数据源

## 方法说明

### Dao接口
```
public interface Dao<T extends Entity> {

    int insert(T entity);

    int insert(Consumer<T> entity);

    int insertNotNull(T entity);

    int insertNotNull(Consumer<T> entity);

    void insertBatch(List<T> entitys);

    int delete(Consumer<T> example);

    <TField> int deleteBy(Function<T, TField> singleField, TField value);

    int deleteById(Object id);

    int deleteWhere(Function<T, Expr> where);

    int updateTraced(Traced<T> traced, Consumer<T> where);

    int updateWhere(Consumer<T> setFields, Function<T, Expr> where);

    int updateDynamicWhere(Consumer<T> entity, Function<T, Expr> where);

    int updateAllFieldWhere(T entity, Function<T, Expr> where);

    int updateNotNullWhere(T entity, Function<T, Expr> where);

    int updateNotNullById(T entity, Object id);

    int updateAllFieldById(T entity, Object id);

    int updateDynamic(Consumer<T> entity, Consumer<T> where);

    int updateDynamicById(Consumer<T> entity, Object id);

    int updateAllField(T entity, Consumer<T> where);

    int updateNotNull(T entity, Consumer<T> where);

    void updateBatch(List<T> entities);

    List<T> findLimit(Consumer<T> where, Function<T, OrderByExpr> orderBy, int limit);

    List<T> findLimit(Consumer<T> where, int limit);

    T findById(Object id);

    List<T> find(Consumer<T> where);

    T findFirst(Consumer<T> where);

    Optional<T> findFirstOptional(Consumer<T> where);

    List<T> findExpr(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int limit);

    List<T> findExpr(Function<T, Expr> where, int limit);

    List<T> findExpr(Function<T, Expr> where);

    Optional<T> findExprFirstOptional(Function<T, Expr> where);

    T findExprFirst(Function<T, Expr> where);

    List<T> findExample(T example, Function<T, OrderByExpr> orderByExpr, ExamplePolicy policy);

    List<T> findExample(T example, ExamplePolicy policy);

    List<T> findExample(T example);

    T findFirstByExample(T example);

    T findFirstByExampleNullable(T example);

    List<Traced<T>> findExprForUpdate(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int limit);

    List<Traced<T>>findExprForUpdate(Function<T, Expr> where, int limit);



    Traced<T> findExprFirstForUpdate(Function<T, Expr> where);

    List<Traced<T>> findForUpdate(Consumer<T> where, Function<T, OrderByExpr> orderBy, int limit);

    List<Traced<T>> findForUpdate(Consumer<T> where, int limit);


    Traced<T> findFirstForUpdate(Consumer<T> where);

    Queryable<T> query();

    Updatable<T> update();

    Paged<T> findPage(Consumer<T> where, Function<T, OrderByExpr> orderBy, int pageIndex, int pageSize);

    Paged<T> findPageExpr(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int pageIndex, int pageSize);
}

  ```

### Queryable接口
```
public interface Queryable<T extends Entity> {

    Queryable<T> errorThrow(RuntimeException throwWhenError);

    Queryable<T> whereExpr(Function<T, Expr> where);

    Queryable<T> whereExpr(boolean predicate, Function<T, Expr> where);

    Queryable<T> whereNotNull(Object predicate, Function<T, Expr> where);

    Queryable<T> whereNotEmpty(String predicate, Function<T, Expr> where);

    Queryable<T> whereNotBlank(String predicate, Function<T, Expr> where);

    Queryable<T> where(Consumer<T> where);

    Queryable<T> whereMap(Map<String,Object> where);

    Queryable<T> unsafeWhereMap(Map<String,Object> where);

    Queryable<T> whereExample(T example);

    Queryable<T> whereExample(T example, ExamplePolicy policy);

    Queryable<T> whereIdEqual(Object id);

    Queryable<T> whereExampleExclude(T example, ExamplePolicy policy, Function<T, List<Object>> excludeFields);

    Queryable<T> orderByFields(Function<T, List<Object>> orderBy, boolean isDesc);

    /**
     * query.orderByAsc(w->list(w.getId()))
     * @param orderBy
     * @return
     */
    Queryable<T> orderByAsc(Function<T, List<Object>> orderBy);

    /**
     * query.orderByDesc(w->list(w.getId()))
     * @param orderBy
     * @return
     */
    Queryable<T> orderByDesc(Function<T, List<Object>> orderBy);

    /**
     *  query.orderByDesc(w->$(w.getId(),desc(),w.getName(),asc()))
     * @param orderByExpr
     * @return
     */
    Queryable<T> orderByExpr(Function<T, OrderByExpr> orderByExpr);

    /**
     * <code>
     *     Map map=new HashMap();
     *     map.put("field name1 or Column Name","desc")
     *     query.orderByMap(map);
     * </code>
     * @param orderByMap
     * @return
     */
    Queryable<T> orderByMap(Map<String, String> orderByMap);

    /**
     * <code>
     *     query.orderByMap(map->{
     *         map.put("field name1 or Column Name","desc")
     *     });
     * </code>
     * @param orderByMap
     * @return
     */
    Queryable<T> orderByMap(Consumer<Map<String, String>> orderByMap);

    Queryable<T> unsafeOrderByMap(Map<String, String> orderByMap);

    /**
     * <code>
     *     query.unsafeOrderByMap(map->{
     *         //这个是不安全的，直接作为sql的一部分,调用方需要自行保证不会被注入
     *         map.put("FD_ID+1","desc")
     *     });
     * </code>
     * @param orderByMap
     * @return
     */
    Queryable<T> unsafeOrderByMap(Consumer<Map<String, String>> orderByMap);

    Queryable<T> select(Function<T, List<Object>> fields);

    Queryable<T> selectOneField(Function<T, Object> field);

    Queryable<T> selectExclude(Function<T, List<Object>> fields);

    Queryable<T> selectDistinct(Function<T, List<Object>> fields);

    <T1> List<Tuple1<T1>> toTuple1(Function<T, Tuple1<T1>> tupleFunc);

    <T1, T2> List<Tuple2<T1, T2>> toTuple2(Function<T, Tuple2<T1, T2>> tupleFunc);

    <T1, T2, T3> List<Tuple3<T1, T2, T3>> toTuple3(Function<T, Tuple3<T1, T2, T3>> tupleFunc);

    <T1, T2, T3, T4> List<Tuple4<T1, T2, T3, T4>> toTuple4(Function<T, Tuple4<T1, T2, T3, T4>> tupleFunc);

    <T1, T2, T3, T4, T5> List<Tuple5<T1, T2, T3, T4, T5>> toTuple5(Function<T, Tuple5<T1, T2, T3, T4, T5>> tupleFunc);

    <T1, T2, T3, T4, T5, T6> List<Tuple6<T1, T2, T3, T4, T5, T6>> toTuple6(Function<T, Tuple6<T1, T2, T3, T4, T5, T6>> tupleFunc);

    <T1, T2, T3, T4, T5, T6, T7> List<Tuple7<T1, T2, T3, T4, T5, T6, T7>> toTuple7(Function<T, Tuple7<T1, T2, T3, T4, T5, T6, T7>> tupleFunc);

    Long count();

    Long countDistinct(Function<T, List<Object>> func);

    List<T> distinct(Function<T, List<Object>> func);

    BigDecimal sum(Function<T,Tuple1<Object>> func);

    BigDecimal avg(Function<T,Tuple1<Object>> func);

    BigDecimal min(Function<T, Tuple1<Object>> func);

    BigDecimal max(Function<T, Tuple1<Object>> func);


    boolean exists();

    T first();

    Traced<T> firstForUpdate();

    List<T> top(int limit);

    List<T> toList();

    <Field1,Field2> Map<Field1,Field2> toDistinctMap(Function<T, Tuple2<Field1, Field2>> func);

    Stream<T> toStream();

    List<T> toList(int limit);

    List<Traced<T>> toListForUpdate(int limit);

    Paged<T> toPage(int pageIndex, int pageSize);

    List<T> toPageCountLess(int pageIndex, int pageSize);

    Paged<T> toPageHLLP(int pageIndex, int pageSize, Function<T, String> idFunc);

    QueryExpr getQueryExpr();
}
```
### Updatable接口
```
public interface Updatable<T extends Entity> {

    /**
     * update 被修改的字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao=null;
     *   Traced<TdCompany> traced = companyDao.findFirstForUpdate(w -> w.setFd_id(100L));
     *   TdCompany company = traced.get();
     *   company.setFd_erpcode("testerpcode");  //被修改的字段
     *   int row = companyDao.update()
     *             .setModified(traced)         //对于没有修改的字段，将会被忽略
     *             .whereIdEqual(company.getFd_id())
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setModified(Traced<T> entity);

    /**
     * update 所有字段(除了id外),如果字段值为null，update到数据库也是null
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");
     *   int row = companyDao.update()
     *             .setAllField(company)         //所有字段(除了id外)都会被update,不管 值有没有被修改
     *             .whereExpr(w->$(w.getFd_id(),eq(),company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setAllField(T entity);

    /**
     * update 所有字段非null字段(除了id外),如果字段值为null，将会被忽略
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");
     *   int row = companyDao.update()
     *             .setNotNull(company)
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setNotNull(T entity);

    /**
     * update 所有字段非null字段或者字符串isNotEmpty(除了id外),如果字段值为null或者empty String，将会被忽略
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");  //将会被update
     *   company.setFd_phone(""); //不会被update
     *   int row = companyDao.update()
     *             .setNotEmpty(company)
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setNotEmpty(T entity);

    /**
     * update 所有字段非null字段或者字符串isNotBlank(除了id外),如果字段值为null或者empty String或者 blank string，将会被忽略
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");  //将会被update
     *   company.setFd_phone("   "); //不会被update
     *   int row = companyDao.update()
     *             .setNotBlank(company)
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setNotBlank(T entity);

    /**
     * update 指定字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .set(w->{
     *                 //只会update这3个字段
     *                 w.setFd_erpcode("000");
     *                 w.setFd_phone(null);  //将会update为null
     *                 w.setFd_del(1L);
     *             })
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> set(Consumer<T> entity);

    /**
     * update 指定字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   String phone="xxxx";
     *   int row = companyDao.update()
     *             .setIf(StringUtils.isNotBlank(phone),w->{
     *                 w.setFd_phone(phone);
     *             })
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param predicate
     * @param entity
     * @return
     */
    Updatable<T> setIf(boolean predicate, Consumer<T> entity);

    Updatable<T> setNotNull(Object predicate, Consumer<T> entity);

    Updatable<T> setNotEmpty(String predicate, Consumer<T> entity);

    Updatable<T> setNotBlank(String predicate, Consumer<T> entity);

    /**
     * update 多个字段（使用java字段名,区分大小）
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   Map map=new HashMap();
     *   map.put("fd_phone","xxx");
     *   int row = companyDao.update()
     *             .setFieldMap(map)
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param fieldMapCaseSentive
     * @return
     */
    Updatable<T> setFieldMap(Map<String, Object> fieldMapCaseSentive);

    Updatable<T> setFieldMap(Consumer<Map<String, Object>> colMapIgnoreCase);

    /**
     * update 多个字段（使用表列名,不区分大小,表字段必须在要实体类存在）
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   Map map=new HashMap();
     *   map.put("FD_PHONE","xxx");
     *   int row = companyDao.update()
     *             .setColumnMap(map)
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param colMapIgnoreCase
     * @return
     */
    Updatable<T> setColumnMap(Map<String, Object> colMapIgnoreCase);


    Updatable<T> setColumnMap(Consumer<Map<String, Object>> colMapIgnoreCase) ;

    /**
     * update 一个字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .setOne(TdCompany::getFd_phone,"xxxx")
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param field
     * @param value
     * @return
     */
    <TField> Updatable<T> setOne(Function<T, TField> field, TField value);

    /**
     * update where
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .set(w->{
     *                 w.setFd_phone("xx");
     *             })
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param where
     * @return
     */
    Updatable<T> where(Consumer<T> where);

    /**
     * where Id 等于
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .set(w->{
     *                 w.setFd_phone("xxx");
     *             })
     *             .whereIdEqual(100L)
     *             .execute();
     * }
     * </pre>
     *
     * @param id
     * @return
     */
    Updatable<T> whereIdEqual(Object id);

    /**
     * where 表达式
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   String phone="xxxx";
     *   int row = companyDao.update()
     *             .setIf(StringUtils.isNotBlank(phone),w->{
     *                 w.setFd_phone(phone);
     *             })
     *             .whereExpr(w-> $(
     *                    $(w.getFd_id(),eq(),100L) ,
     *                    and() ,
     *                    $(w.getFd_del(),eq(), 1L)
     *             )).execute();
     * }
     * </pre>
     *
     * @param where
     * @return
     */
    Updatable<T> whereExpr(Function<T, Expr> where);

    /**
     * 执行sql
     *
     * @return
     */
    int execute();
}
```