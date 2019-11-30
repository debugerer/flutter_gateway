



#ifndef MSTEnterprise_getgateway_h
#define MSTEnterprise_getgateway_h
#include <TargetConditionals.h>




#if  TARGET_IPHONE_SIMULATOR
//#include <net/route.h>
#include "route.h"
#elif TARGET_OS_IPHONE
#include "route.h"
#endif



 

unsigned char * getdefaultgateway(in_addr_t * addr);

#endif
