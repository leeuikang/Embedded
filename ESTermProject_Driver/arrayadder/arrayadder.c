#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/delay.h>
#include <asm/io.h>
#include <asm/uaccess.h>

#define DRIVER_AUTHOR		"es"
#define DRIVER_DESC		"es term project"
#define ADDER_MAJOR		270
#define ADDER_NAME		"ARRAYADDER"
#define ADDER_MODULE_VERSION	"ARRAYADDER V0.1"

#define BUZZER_ADDRESS		0x88000050
#define BUZZER_ADDRESS_RANGE	0x1000

#define SEGMENT_ADDRESS_GRID	0x88000030
#define SEGMENT_ADDRESS_DATA	0x88000032
#define SEGMENT_ADDRESS_1	0x88000034
#define SEGMENT_ADDRESS_RANGE	0x1000
#define MODE_0_TIMER_FORM	0x0
#define MODE_1_CLOCK_FORM	0x1

static int buzzer_usage = 0;
static unsigned long *buzzer_ioremap;

static unsigned int segment_usage = 0;
static unsigned long *segment_data;
static unsigned long *segment_grid;


int adder_open (struct inode *inode,struct file *filep) {
	if( segment_usage != 0 || buzzer_usage != 0 )
		return -EBUSY;

	buzzer_ioremap = ioremap(BUZZER_ADDRESS, BUZZER_ADDRESS_RANGE);
	
	segment_grid = ioremap(SEGMENT_ADDRESS_GRID, SEGMENT_ADDRESS_RANGE);
	segment_data = ioremap(SEGMENT_ADDRESS_DATA, SEGMENT_ADDRESS_RANGE);

	if(!check_mem_region((unsigned long)buzzer_ioremap, BUZZER_ADDRESS_RANGE)) {
		request_mem_region((unsigned long)buzzer_ioremap, BUZZER_ADDRESS_RANGE, ADDER_NAME);
	}
	else {
		printk(KERN_WARNING"Can't get IO Region 0x%x\n", (unsigned int)buzzer_ioremap);
	}
	buzzer_usage = 1;

	if(!check_mem_region((unsigned long)segment_data, SEGMENT_ADDRESS_RANGE) && !check_mem_region((unsigned long)segment_grid, SEGMENT_ADDRESS_RANGE)) {
		request_region((unsigned long)segment_grid, SEGMENT_ADDRESS_RANGE, ADDER_NAME);
		request_region((unsigned long)segment_data, SEGMENT_ADDRESS_RANGE, ADDER_NAME);
	}
	else {
		printk(KERN_WARNING"driver: unable to register this\n");
	}
	segment_usage = 1;
	return 0;
}


int adder_release (struct inode *inode, struct file *filep) {
	iounmap(buzzer_ioremap);
	iounmap(segment_grid);
	iounmap(segment_data);

	release_mem_region((unsigned long)buzzer_ioremap, BUZZER_ADDRESS_RANGE);
	release_region((unsigned long)segment_data, SEGMENT_ADDRESS_RANGE);
	release_region((unsigned long)segment_grid, SEGMENT_ADDRESS_RANGE);

	buzzer_usage = 0;
	segment_usage = 0;

	return 0;
}


//for segment control
ssize_t adder_write(struct file *inode, const char *gdata, size_t length, loff_t *off_what) {
	unsigned int ret;
	unsigned char data[2];

	ret = copy_from_user(&data, gdata, 2);

	*segment_grid = data[0];
	*segment_data = data[1];
	mdelay(1);

	*segment_data = 0;

	return length;
}


//for buzzer control
int adder_ioctl(struct inode *inode, struct file *filep, unsigned int cmd, unsigned long arg) {
	unsigned char *addr;
	unsigned char c;

	if( cmd == 1 ) {
		c = 1;
	}
	else {
		c = 0;
	}

	addr = (unsigned char *)(buzzer_ioremap);
	*addr = c;

	return 0;	
}


static struct file_operations adder_fops = {
	.owner		= THIS_MODULE,
	.open		= adder_open,
	.write		= adder_write,
	.release	= adder_release,
	.ioctl		= adder_ioctl,
};


int adder_init(void) {
	int result;

	result = register_chrdev(ADDER_MAJOR, ADDER_NAME, &adder_fops);
	if( result < 0 ) {
		printk(KERN_WARNING"Cant get any major\n");
		return result;
	}

	printk(KERN_WARNING"Init Module, Adder Major number : %d\n", ADDER_MAJOR);
	return 0;
}


void adder_exit(void) {
	unregister_chrdev(ADDER_MAJOR, ADDER_NAME);

	printk("driver: %s DRIVER EXIT\n", ADDER_NAME);
}


module_init(adder_init);
module_exit(adder_exit);

MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
MODULE_LICENSE("Dual BSD/GPL");
